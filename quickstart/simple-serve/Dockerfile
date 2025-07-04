# 第二阶段：生成最终镜像，基于轻量级 Alpine 镜像，并安装 curl
FROM alpine:latest

# 更新 apk 索引并安装 curl
RUN apk update && apk add --no-cache curl

# 设定工作目录
WORKDIR /app

# 将编译好的二进制文件从 builder 阶段复制到当前镜像中
COPY dist/serve .

# 暴露应用程序监听的端口（例如 8080）
EXPOSE 8080

# 设置容器启动时执行的命令
ENTRYPOINT ["/app/serve"]