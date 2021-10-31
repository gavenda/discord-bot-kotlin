FROM openjdk:16-jdk
RUN mkdir /bot
COPY ./build/install/bot/ /bot/
WORKDIR /bot/discord-bot/bin
CMD ["./discord-bot"]