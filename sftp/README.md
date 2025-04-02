```shell
# SFTP 连接测试
sftp -P 2222 -i ~/.ssh/user1_private_key user1@<service-ip>

# 检查目录隔离
sftp> pwd
/sftp-data/user1
sftp> cd ..
Couldn't canonicalize: Failure
```

## 生成 HostKey
```shell
ssh-keygen -t rsa -b 4096 -f /etc/ssh/ssh_host_rsa_key -N ""
ssh-keygen -t ecdsa -b 521 -f /etc/ssh/ssh_host_ecdsa_key -N ""
ssh-keygen -t ed25519 -f /etc/ssh/ssh_host_ed25519_key -N ""
```

关键概念解释
Chroot 监狱：

用户被限制在自己的目录中（如 /sftp-data/user1）

无法访问上级目录，保证文件隔离

权限数字含义：

755 = 所有者：读+写+执行 (7)，其他人：读+执行 (5)

700 = 只有所有者有全部权限

600 = 只有所有者可读/写

/sbin/nologin：

用户无法通过 SSH 执行命令或登录 Shell

但可以通过 SFTP 传输文件

HostKey 持久化：

避免容器重启后密钥变化导致客户端报错

通过 PVC 将密钥存储在 OpenShift 持久卷中
