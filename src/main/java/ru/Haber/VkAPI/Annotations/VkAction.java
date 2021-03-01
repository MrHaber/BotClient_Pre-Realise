package ru.Haber.VkAPI.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.Haber.VkAPI.bootstrap.BotClient.VkHandler;





@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VkAction {

	VkHandler.VkHandlerType type() default VkHandler.VkHandlerType.COMMAND;
	
}
