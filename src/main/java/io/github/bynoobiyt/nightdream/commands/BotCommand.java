package io.github.bynoobiyt.nightdream.commands;

import java.lang.annotation.*;

/**
 * Annotation for a Command
 * @author Daniel Schmid
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface BotCommand{
	/**
	 * the aliases of the Command
	 * @return aliases of the Command
	 */
	String[] value();
}
