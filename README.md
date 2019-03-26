# cryptic-java-lib

This is a microservice-libary for java of cryptic-game.

## Quick-Start

The [EchoMicroService.java](https://github.com/cryptic-game/server/blob/master/test/main/java/net/cryptic_game/server/EchoMicroService.java) implements an `EchoMicroService`.  

## Maven

To import this libary you can use [maven](https://maven.apache.org/) like this:

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
	
	...
	
<dependency>
	<groupId>com.github.cryptic-game</groupId>
	<artifactId>java-lib</artifactId>
	<version>0.0.3-SNAPSHOT</version>
</dependency>
```

The repository is hostet by [JitPack](https://jitpack.io/#cryptic-game/java-lib).

## Test it!

Your microservice will be supportet by the [game-server of cryptic](https://github.com/cryptic-game/server).  

### Environment variables

| key               | default value |  
|-------------------|---------------|  
| MSSOCKET_HOST     | 127.0.0.1     |  
| MSSOCKET_PORT     | 1239          |  
| STORAGE_LOCATION  | data/         |  

## Wiki

Visit our [wiki](https://github.com/cryptic-game/java-lib/wiki) for more information. 