# 基础镜像：Nginx
FROM nginx:stable-alpine

# 1. 也就是把本地打包好的 dist 文件夹，复制到 Nginx 的默认目录
# 注意：你需要先在本地执行 npm run build
COPY dist/ /usr/share/nginx/html/

# 2. 把我们写的 nginx.conf 复制进去，覆盖默认配置
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露 80 端口
EXPOSE 80