#!/bin/bash

git clone https://github.com/byNoobiYT/NightDream.wiki.git wiki
#chmod +x gradlew
gradle jar
cp build/libs/Night*.jar wiki/NightDream.jar
cd wiki
git add NightDream.jar
git commit -m "CI JAR deploy: `date`"
git push https://$githubUsername:$githubToken@github.com/byNoobiYT/NightDream.wiki.git