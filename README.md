# cryptic-java-lib [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=cryptic-game_java-lib&metric=coverage)](https://sonarcloud.io/dashboard?id=cryptic-game_java-lib)

This is a microservice-library for java of cryptic-game.

## Quick-Start

Just use the [microservice template](https://github.com/cryptic-game/microservice-java-template) to use the latest version of this library.

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

<dependencies>
	<dependency>
		<groupId>com.github.cryptic-game</groupId>
		<artifactId>java-lib</artifactId>
		<version>master-SNAPSHOT</version>
	</dependency>
</dependencies>
```

The repository is hosted by [JitPack](https://jitpack.io/#cryptic-game/java-lib).

## Test it!

Your microservice will be supported by the [game-server of cryptic](https://github.com/cryptic-game/server).  

## Environment variables

| Key               | Default Value |  
|-------------------|---------------|  
| MSSOCKET_HOST     | 127.0.0.1     |  
| MSSOCKET_PORT     | 1239          |  
| MYSQL_HOSTNAME    | cryptic       |
| MYSQL_PORT        | 3306          |
| MYSQL_USERNAME    | cryptic       |
| MYSQL_PASSWORD    | cryptic       |
| MYSQL_DATABASE    | cryptic       |
| STORAGE_LOCATION  | data/         |  

## Wiki

Visit our [wiki](https://github.com/cryptic-game/java-lib/wiki) for more information. 
