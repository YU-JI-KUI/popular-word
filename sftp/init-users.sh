#!/bin/sh

# 创建用户组
if ! getent group sftpusers >/dev/null; then
    groupadd -g 5000 sftpusers
fi

# 遍历 ConfigMap 用户配置
for USER_FILE in /etc/sftp-users/*.yaml; do
    USER_NAME=$(grep 'username:' "$USER_FILE" | awk '{print $2}')
    USER_UID=$(grep 'uid:' "$USER_FILE" | awk '{print $2}')
    USER_PUBKEY=$(grep 'public_key:' "$USER_FILE" | sed 's/public_key: //')

    # 创建用户
    if ! id -u "$USER_NAME" >/dev/null 2>&1; then
        useradd -l -M -d "/sftp-data/$USER_NAME" \
                -u "$USER_UID" -g sftpusers \
                -s /sbin/nologin "$USER_NAME"
    fi

    # 创建用户目录
    USER_HOME="/sftp-data/$USER_NAME"
    mkdir -p "$USER_HOME/.ssh"
    echo "$USER_PUBKEY" > "$USER_HOME/.ssh/authorized_keys"

    # 设置权限
    chown -R "$USER_NAME:sftpusers" "$USER_HOME"
    chmod 700 "$USER_HOME/.ssh"
    chmod 600 "$USER_HOME/.ssh/authorized_keys"
done