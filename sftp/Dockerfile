# 使用 Alpine Linux 3.18 作为基础镜像（轻量级 Linux 发行版）
FROM alpine:3.18

# 安装必要软件包：
# - openssh-server: SSH 服务端程序
# - shadow: 提供用户管理工具（如 useradd）
# - aws-cli: 用于 S3 文件同步（根据需求可选）
# - inotify-tools: 文件监控工具（用于实时同步）
RUN apk add --no-cache openssh-server shadow aws-cli inotify-tools

# 将本地的 sshd_config 文件复制到容器内的 /etc/ssh/ 目录
# 这个文件是 SSH 服务器的配置文件
COPY sshd_config /etc/ssh/sshd_config

# 复制入口脚本和用户初始化脚本到容器根目录
COPY entrypoint.sh /entrypoint.sh
COPY init-users.sh /init-users.sh

# 设置脚本可执行权限：
# chmod +x 让文件变成可执行程序
# && 表示前一个命令成功后执行下一个命令
RUN chmod +x /entrypoint.sh /init-users.sh && \
    # 创建 HostKey 存储目录
    mkdir -p /etc/ssh/host_keys && \
    # 生成默认 HostKey（首次启动时生成，之后会被覆盖）
    ssh-keygen -A -f /etc/ssh/host_keys

# 声明数据卷（用于持久化用户数据和 HostKey）
# /sftp-data 是用户文件的存储目录
VOLUME ["/sftp-data"]

# 指定容器启动时执行的入口脚本
ENTRYPOINT ["/entrypoint.sh"]
