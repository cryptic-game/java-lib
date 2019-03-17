# cryptic-java-lib

This is a microservice-libary for java of cryptic-game.

## Quick-Start

The following code snippet implements an `EchoMicroService`.  
So every data will be instantly send back to source.   

```java
public class EchoMicroService extends MicroService {

	public EchoMicroService() {
		super("echo");
	}

	@Override
	public JSONObject handle(String[] endpoint, JSONObject data) {
		return data; // sends the received data instantly back to sender
	}
	
	public static void main(String[] args) {
		new EchoMicroService();
	}

}
```

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
	<version>0.0.1-SNAPSHOT</version>
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