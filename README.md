# Discord Kotlin Bot Template

A simple discord bot template in Kotlin. Intended to run on multiple machines with its own shard id.

## What does it have?
- Dependency Injection using [KODEIN](https://kodein.org/)
- Logging using Log4J2
- Docker support
- Included Kotlin helpers from [jda-ktx](https://github.com/MinnDevelopment/jda-ktx)

## Get me up and running!
Rename `example.env` to `.env` and put your discord bot token. Do not forget to set the shard id 
and the total number of shards you want to deploy.

NOTE: Sharding is assumed to be a single instance of a machine, hence the need to provide the shard id.

```bash
./gradlew installDist
docker-compose build
docker-compose up
```