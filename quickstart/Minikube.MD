### 通过 minikube 部署集群

#### 保证 hyperkit 环境

#### 启动命令

minikube start -d=hyperkit --cni=bridge -c=docker -n=2 --cpus=2 --memory=4096

#### 配置镜像
