#!/bin/sh  # 指定使用 Shell 解释器

# 创建 sftpusers 用户组（如果不存在）
# getent 检查组是否存在，> /dev/null 将输出丢弃
if ! getent group sftpusers >/dev/null; then
    # groupadd 创建组，-g 5000 指定组 ID
    groupadd -g 5000 sftpusers
fi

# 遍历 ConfigMap 中的用户配置文件（/etc/sftp-users/*.yaml）
for USER_FILE in /etc/sftp-users/*.yaml; do
    # 从 YAML 文件中提取用户名、UID 和公钥
    USER_NAME=$(grep 'username:' "$USER_FILE" | awk '{print $2}')
    USER_UID=$(grep 'uid:' "$USER_FILE" | awk '{print $2}')
    USER_PUBKEY=$(grep 'public_key:' "$USER_FILE" | sed 's/public_key: //')

    # 如果用户不存在则创建
    if ! id -u "$USER_NAME" >/dev/null 2>&1; then
        # useradd 参数详解：
        # -l : 不将用户添加到 lastlog 数据库
        # -M : 不创建用户家目录（后面手动创建）
        # -d : 指定用户家目录路径
        # -u : 指定用户 UID（必须唯一）
        # -g : 指定主组为 sftpusers
        # -s /sbin/nologin : 禁止用户登录 Shell
        useradd -l -M -d "/sftp-data/$USER_NAME" \
                -u "$USER_UID" -g sftpusers \
                -s /sbin/nologin "$USER_NAME"
    fi

    # 创建用户专属目录结构
    USER_HOME="/sftp-data/$USER_NAME"
    mkdir -p "$USER_HOME/.ssh"           # -p 表示自动创建父目录
    echo "$USER_PUBKEY" > "$USER_HOME/.ssh/authorized_keys"

    # 设置权限：
    # chown 修改文件所有者（用户:组）
    # chmod 700 表示只有所有者可读/写/执行
    # chmod 600 表示只有所有者可读/写
    chown -R "$USER_NAME:sftpusers" "$USER_HOME"
    chmod 700 "$USER_HOME/.ssh"
    chmod 600 "$USER_HOME/.ssh/authorized_keys"
done
