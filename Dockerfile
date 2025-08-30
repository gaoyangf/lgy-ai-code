# 使用官方 OpenJDK 运行时作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制 jar 包到容器中
COPY target/lgy-ai-code-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 创建数据目录
RUN mkdir -p /app/logs /app/data

# 运行应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]
