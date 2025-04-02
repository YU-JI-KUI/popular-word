FROM alpine:3.18

# 安装依赖
RUN apk add --no-cache openssh-server shadow aws-cli inotify-tools

# 配置 SSH
COPY sshd_config /etc/ssh/sshd_config
COPY entrypoint.sh /entrypoint.sh
COPY init-users.sh /init-users.sh

# 设置权限
RUN chmod +x /entrypoint.sh /init-users.sh && \
    mkdir -p /etc/ssh/host_keys && \
    ssh-keygen -A -f /etc/ssh/host_keys  # 预生成默认 HostKey（首次启动覆盖）

# 工作目录
VOLUME ["/sftp-data"]

ENTRYPOINT ["/entrypoint.sh"]