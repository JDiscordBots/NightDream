Nightdream is a clone of the Bot [Daydream](https://gitlab.com/botstudio/daydream/) in Java.

## used Libraries
* [JDA](https://github.com/DV8FromTheWorld/JDA/) (in order to interact with Discord)
* [reflections](https://code.google.com/archive/p/reflections/) (for the Command mechanism)
* [KSoft4J](http://github.com/kSoft-Si/KSoft4J/) (for interacting with the KSoft API)
* [Apache Commons Text](https://github.com/apache/commons-text) (for string similarity)
* [slf4j](https://github.com/qos-ch/slf4j/) (because JDA logs using slf4j)
* [NightDream-Logging](https://github.com/JDiscordBots/NightDream-Logging) (Logging)
* [Jedis](https://github.com/xetorthio/jedis) (Redis Storage)
* [jda-testing-system](https://github.com/JDiscordBots/jda-testing-system) (for automated tests)
* [Javassist](https://github.com/jboss-javassist/javassist) (for the eval command)
* [dbio-java](https://github.com/zastrixarundell/dbio-java) (for the bio command)

## testing libraries
* [JUnit](https://junit.org/junit5/) (for feature tests(not unit tests))
* [Awaitility](https://github.com/awaitility/awaitility) (in order to wait exponentially long until a response is received)

## transitive dependencies
If you want to see the transitive dependencies run `mvn dependency:tree`.