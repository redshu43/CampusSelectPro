# 基础镜像：JDK17
FROM eclipse-temurin:17-jdk-alpine
# 作者：me
LABEL maintainer="lcy"

# 将打包好的 jar 包复制进去， 重命名为app.jar
# 先在本地执行一下mvn clean package 确保target下的jar包是最新的
COPY target/*.jar app.jar

# 容器内暴露端口
EXPOSE 8080

# 启动命令：指定使用 docker 配置文件
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=docker"]