#!/bin/bash
set -e  # 出错则退出

# 配置变量
IMAGE_NAME="serve"
IMAGE_TAG="latest"
OUTPUT_DIR="dist"
BINARY_NAME="serve"
DEPLOY_FILE="serve.yml"
# 第一步：创建输出目录
echo "【INFO】创建目录 ${OUTPUT_DIR} ..."
mkdir -p "${OUTPUT_DIR}"

# 第二步：使用 go build 编译程序
echo "【INFO】编译 Go 程序..."
# 如果需要生成静态编译的二进制，建议设置 CGO_ENABLED=0
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o "${OUTPUT_DIR}/${BINARY_NAME}" .
if [ $? -ne 0 ]; then
    echo "【ERROR】Go 编译失败，退出！"
    exit 1
fi
echo "【INFO】编译成功，生成二进制：${OUTPUT_DIR}/${BINARY_NAME}"

# 第三步：构建 Docker 镜像
echo "【INFO】开始构建 Docker 镜像 ${IMAGE_NAME}:${IMAGE_TAG} ..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
if [ $? -ne 0 ]; then
    echo "【ERROR】Docker 镜像构建失败，退出！"
    exit 1
fi
echo "【INFO】Docker 镜像构建成功：${IMAGE_NAME}:${IMAGE_TAG}"

# 第四步：部署
echo "【INFO】开始部署 ${BINARY_NAME} 服务节点 ..."
kubectl apply -f ${DEPLOY_FILE}
if [ $? -ne 0 ]; then
    echo "【ERROR】部署 ${BINARY_NAME} 服务失败，退出！"
    exit 1
fi
echo "【INFO】部署 ${BINARY_NAME} 服务节点成功 "