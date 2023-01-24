FROM alpine:3.14
WORKDIR /www
RUN apk update
RUN apk add openjdk11
RUN apk add git && git clone https://github.com/ATer-Oganisyan/otushomework.git && cd otushomework && javac OtusHttpServer.java && apk del git && rm OtusHttpServer.java
CMD ["java", "-classpath", "/www/otushomework", "OtusHttpServer"]