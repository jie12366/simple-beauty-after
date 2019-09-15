# 该镜像需要依赖的基础镜像
FROM java:8
# 创建/tmp目录并持久化到Docker数据文件夹
VOLUME /tmp
# 将当前目录下的jar包复制到docker容器的/目录下
ADD blog-0.0.1-SNAPSHOT.jar /blog.jar
# 运行过程中创建一个mall-tiny-docker-file.jar文件
RUN bash -c 'touch /blog.jar'
# 声明服务运行在8080端口
EXPOSE 81
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/blog.jar"]
# 指定维护者的名字
MAINTAINER jie
