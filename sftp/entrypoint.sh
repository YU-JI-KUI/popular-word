#!/bin/sh

# 持久化 HostKey（链接到持久化目录）
rm -rf /etc/ssh/ssh_host_*
ln -s /etc/ssh/host_keys/* /etc/ssh/

# 初始化用户
echo "Initializing users..."
/init-users.sh

# 修复 Chroot 目录权限
find /sftp-data -mindepth 1 -maxdepth 1 -type d -exec chown root:root {} \;
find /sftp-data -mindepth 1 -maxdepth 1 -type d -exec chmod 755 {} \;

# 启动 SSH 服务
echo "Starting SSH Daemon..."
exec /usr/sbin/sshd -D -e "$@"