package ru.Haber.VkAPI.Annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface BotConfigurableHandler {
	
	String path() default " ";
	
	String id();
	
	String token();
	
}
