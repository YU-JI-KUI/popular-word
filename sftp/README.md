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