# BotClient Pre realise, custom build for Vk library
## VK-API Longpoll version <= 5.101
# Using:
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
}
	@AsyncInit(asyncId = 1)
	public static class CommandDispatch implements VkCommandExecutor {

		@Override
		public void onCommand(@NotNull IBotUtils bot, @NotNull VkApiClient client, @NotNull GroupActor actor,
				@NotNull Message message, @NotNull String[] args) {
				// TODO: Works code there, when user send command in VK chat to bot
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
