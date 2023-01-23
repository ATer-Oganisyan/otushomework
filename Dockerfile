FROM openjdk:8-jdk-alpine
WORKDIR /www
RUN apk add git
RUN cd /www && git clone https://github.com/ATer-Oganisyan/otushomework.git && cd otushomework && javac OtusHttpServer.java && apk del git && rm OtusHttpServer.java
CMD ["java", "-classpath", "/www/otushomework", "OtusHttpServer"]