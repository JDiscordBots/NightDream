#!/bin/bash

git clone https://github.com/byNoobiYT/NightDream.wiki.git wiki
mvn package
cp target/NightDream.jar wiki/NightDream.jar
cd wiki
git add NightDream.jar
git commit -m "CI JAR deploy: `date`"
git push https://$githubUsername:$githubToken@github.com/JDiscordBots/NightDream.wiki.git
