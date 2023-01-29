[![](https://jitpack.io/v/MrHaber/BotClient_Pre-Realise.svg)](https://jitpack.io/#MrHaber/BotClient_Pre-Realise)
# BotClient Pre realise, custom build for Vk library
## VK-API Longpoll version <= 5.101



### Example
<img src="data/Screenrecorder-2022-08-20-08-01-19-789.mp4.gif" alt= ""  width="250"/>

# Using:
[Extended description(russian)](https://vk.com/@-191873806-how-to-basic)
### Startup
```java
@BotHandler(token = "token", id = 12345) // Token and ID for init botclient
public class BotClientTest extends BotClient{

	private static final BotClientTest bot = newLocalExec(BotClientTest::new, true); // initialize bootstrap
	
	public BotClientTest() {
		// empty constructor, flag for compiler
	}
	@Override
	public void onStart() {
		
		debug(true); // debug mode, for expand information when program works
		
		systemLocale("ru"); // localization for activate autotranslate cluster
		
		getInstaller().installHandler(new CommandDispatch()); // Init program
		
	}
	@AsyncInit(asyncId = 1)
	public static class CommandDispatch implements VkCommandExecutor {

		@Override
		public void onCommand(@NotNull IBotUtils bot, @NotNull VkApiClient client, @NotNull GroupActor actor,
				@NotNull Message message, @NotNull String[] args) {
				// TODO: Works code there, when user send command in VK chat to bot
		}
	}
}
```
### Maven
```xml
<repositories>
   <repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
	</repository>
</repositories>

	<dependency>
	    <groupId>com.github.MrHaber</groupId>
	    <artifactId>BotClient_Pre-Realise</artifactId>
	    <version>version_from_github</version>
	</dependency>
```
### Gradle(Groovy)
```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
	
	dependencies {
	        implementation 'com.github.MrHaber:BotClient_Pre-Realise:version_from_github'
	}
```

### Disable internal vk logger for external projects

### Maven
```xml
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-api</artifactId>
      <version>2.11.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-core</artifactId>
      <version>2.11.2</version>
    </dependency>
```
### Gradle(Groovy)
```groovy
dependencies {
    //Binding for Log4J -->
    compile group: 'org.apache.logging.log4j', name: 'log4j-slf4j-impl', version: '2.11.2'
    
    //Log4j API and Core implementation required for binding
    compile group: 'org.apache.logging.log4j', name: 'log4j-api', version: '2.11.2'
    compile group: 'org.apache.logging.log4j', name: 'log4j-core', version: '2.11.2'
}
```
After all, go to main class and setting up logger level for HttpTransportClient
 ```java
Configurator.setLevel("com.vk.api.sdk.httpclient.HttpTransportClient",Level.WARN);
```
