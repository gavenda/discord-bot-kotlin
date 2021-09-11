FROM openjdk:16-jdk
RUN mkdir /app
COPY ./build/install/bot/ /app/
WORKDIR /app/bin
CMD ["./bot"]