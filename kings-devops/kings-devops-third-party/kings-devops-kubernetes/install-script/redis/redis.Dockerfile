# 使用官方的 Redis 镜像作为基础镜像
FROM redis:latest
 
# 更新包列表并安装 curl
RUN apt-get update && apt-get install -y curl
 
# 你可以在这里添加更多的命令，例如设置环境变量、复制文件等