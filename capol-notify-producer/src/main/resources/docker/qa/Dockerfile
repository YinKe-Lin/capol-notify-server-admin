# Docker image for springboot file run
# VERSION 0.0.1
# Author: hpq
# 基础镜像使用java
FROM 10.1.245.95/java/java:8u282
# 作者
MAINTAINER capol
WORKDIR /var/lib/jenkins/workspace/pipeline_notify_producer
VOLUME /tmp
# 将jar包添加到容器中并更名为notify_producer_app.jar
ADD ./capol-notify-producer/target/capol-notify-producer.jar notify_producer_app.jar
# 运行jar包
RUN bash -c 'touch /notify_producer_app.jar'
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

ENTRYPOINT java ${JAVA_OPTS} ${JAVA_AGENT} -Djava.security.egd=file:/dev/./urandom -jar notify_producer_app.jar --spring.profiles.active=qa --spring.cloud.consul.discovery.default-query-tag=env=qa --spring.cloud.consul.discovery.tags=env=qa --spring.cloud.consul.host=${NODE_IP}
