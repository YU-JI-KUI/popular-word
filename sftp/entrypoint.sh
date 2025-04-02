#!/bin/sh  # 指定使用 Shell 解释器执行本脚本

# 删除默认的 HostKey 文件（可能由 Dockerfile 生成）
rm -rf /etc/ssh/ssh_host_*

# 创建符号链接，将持久化的 HostKey 链接到 SSH 默认位置
# 例如：/etc/ssh/host_keys/ssh_host_rsa_key -> /etc/ssh/ssh_host_rsa_key
ln -s /etc/ssh/host_keys/* /etc/ssh/

# 输出初始化提示信息
echo "Initializing users..."

# 执行用户初始化脚本
/init-users.sh

# 修复 Chroot 目录权限：
# - find 命令查找所有用户目录
# - chown root:root 设置目录所有者为 root（SSH 强制要求）
# - chmod 755 设置权限：所有者可读/写/执行，其他人只读/执行
find /sftp-data -mindepth 1 -maxdepth 1 -type d -exec chown root:root {} \;
find /sftp-data -mindepth 1 -maxdepth 1 -type d -exec chmod 755 {} \;

# 启动 SSH 服务：
# - exec 用当前进程替换为 sshd，保证 PID 1 是 sshd
# - -D 表示保持前台运行（不变成守护进程）
# - -e 表示将日志输出到 stderr（方便在容器日志中查看）
echo "Starting SSH Daemon..."
exec /usr/sbin/sshd -D -e "$@"
