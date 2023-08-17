# Docker image for springboot file run
# VERSION 0.0.1
# Author: heyong
# 基础镜像使用java
FROM 10.1.245.95/java/java:8u282
# 作者
MAINTAINER heyong<heyong1@capol.cn>
WORKDIR /var/lib/jenkins/workspace/pipeline_notify_job
VOLUME /tmp
# 将jar包添加到容器中并更名为notify_job_app.jar
ADD  ./capol-notify-job/target/capol-notify-job.jar notify_job_app.jar
# 运行jar包
RUN bash -c 'touch /notify_job_app.jar'
# 设置时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone


ENTRYPOINT java ${JAVA_OPTS} ${JAVA_AGENT} -Djava.security.egd=file:/dev/./urandom -jar notify_job_app.jar --spring.profiles.active=pet --spring.cloud.consul.discovery.default-query-tag=env=pet --spring.cloud.consul.discovery.tags=env=pet --spring.cloud.consul.host=${NODE_IP}