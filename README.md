# BotClient Pre realise, custom build for Vk library

# Using:
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
