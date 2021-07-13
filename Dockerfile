FROM centos:7.6.1810

EXPOSE 9001

RUN yum -y install epel-release && yum -y install python-pip && pip install supervisor
RUN mkdir -p /etc/supervisor

ENTRYPOINT ["supervisord", "-c", "/etc/supervisor/supervisord.conf"]


