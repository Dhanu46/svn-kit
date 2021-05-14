FROM mcr.microsoft.com/java/jre:11-zulu-alpine AS builder
#RUN addgroup -S svn-kit && adduser -S dhanush -G svn-kit
#USER dhanush:svn-kit
WORKDIR source
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM mcr.microsoft.com/java/jre:11-zulu-alpine
WORKDIR application
COPY --from=builder source/dependencies/ ./
COPY --from=builder source/spring-boot-loader/ ./
COPY --from=builder source/snapshot-dependencies/ ./
COPY --from=builder source/application/ ./
ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]

