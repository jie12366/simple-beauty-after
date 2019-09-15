# 该镜像需要依赖的基础镜像
FROM java:8
FROM maven:3.5.4-alpine
# 打包
RUN mvn clean package
# 创建/tmp目录并持久化到Docker数据文件夹
VOLUME /tmp
# 将当前目录下的jar包复制到docker容器的/目录下
ADD target/blog-0.0.1-SNAPSHOT.jar /blog.jar
# 声明服务运行在81端口
EXPOSE 81
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/blog.jar"]
# 指定维护者的名字
MAINTAINER jie
