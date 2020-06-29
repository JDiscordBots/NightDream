# NightDream ![](https://github.com/JDiscordBots/NightDream/workflows/test%20and%20upload/badge.svg) ![](https://github.com/JDiscordBots/NightDream/workflows/static%20code%20analysis/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=NightDream&metric=alert_status)](https://sonarcloud.io/dashboard?id=NightDream) ![Public Instance Status](https://top.gg/api/widget/status/596643235523330070.png) [![Release](https://jitpack.io/v/JDiscordBots/NightDream.svg)](https://jitpack.io/#JDiscordBots/NightDream)
a Clone of the Discord Bot [Daydream](https://gitlab.com/botstudio/daydream) in Java

This is not an exact clone, there are a few differences to the original.

## Invite Nightdream to your Server
[Invite](https://discord.com/oauth2/authorize?client_id=596643235523330070&permissions=8&scope=bot)

## Nightdream on [DBL/top.gg](https://top.gg/)
[Link](https://top.gg/bot/596643235523330070)

[Vote for Nightdream](https://top.gg/bot/596643235523330070/vote)

## Host NightDream by yourself
### Download
You can download NightDream [from the wiki](https://github.com/JDiscordBots/Nightdream/wiki/NightDream.jar) or [from jitpack.io](https://jitpack.io/com/github/JDiscordBots/NightDream/master-SNAPSHOT/NightDream-master-SNAPSHOT.jar)

### Setup
* Run *NightDream.jar* (double-click it or execute `java -jar NightDream.jar`)
* A directory *NightDream* with a file *NightDream.properties* should be created automatically
* paste your Discord Bot token right after *token=*
* You can also change various configurations in this file.
* You can now run the Bot again (double-click *NightDream.jar* or execute `java -jar NightDream.jar`)

### Requirements for hosting NightDream
* JRE >= 1.8.0_221

## Build NightDream on your own
* execute `mvn clean package -DskipTests=true` in the project directory
* the executable JAR file is in the directory `target`

### Requirements for building NightDream
* JDK >= 8
* Maven 3
