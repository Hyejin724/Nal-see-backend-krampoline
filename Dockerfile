# Java 17을 사용하는 OpenJDK 이미지를 기반으로 함
FROM openjdk:17-jdk

# 환경변수 받기
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG KAKAO_CLIENT_ID
ARG KAKAO_CLIENT_SECRET
ARG AWS_ACCESS_KEY
ARG AWS_SECRET_KEY

# 환경변수 설정

ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD
ENV KAKAO_CLIENT_ID=$KAKAO_CLIENT_ID
ENV KAKAO_CLIENT_SECRET=$KAKAO_CLIENT_SECRET
ENV AWS_ACCESS_KEY=$AWS_ACCESS_KEY
ENV AWS_SECRET_KEY=$AWS_SECRET_KEY

WORKDIR /home/gradle/project
COPY . .

# gradlew를 이용한 프로젝트 필드
RUN ./gradlew clean build -x test

# 빌드 결과 jar 파일을 실행
CMD ["java", "-jar", "-Dspring.profiles.active=kram", "/home/gradle/project/build/libs/nalsee-backend-0.0.1-SNAPSHOT.jar"]
