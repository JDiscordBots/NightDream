package io.github.bynoobiyt.nightdream.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bynoobiyt.nightdream.commands.Command;
import io.github.bynoobiyt.nightdream.core.CommandParser.CommandContainer;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
/**
 * executed by a listener when Message sent which begins with the Bot prefix
 * @author Daniel Schmid
 */
public class CommandHandler {
	private static final Map<String, Command> commands = new HashMap<>();
	
	private static final Logger LOG=LoggerFactory.getLogger(CommandHandler.class);
	
	private CommandHandler() {
		//no instantiation
	}
	
	public static Map<String, Command> getCommands() {
		return Collections.unmodifiableMap(commands);
	}
	static void addCommand(String name,Command cmd) {
		commands.put(name, cmd);
	}
	/**
	 * loads Command and executes it
	 * @param cmd the Command as {@link CommandContainer}
	 */
	public static void handleCommand(final CommandParser.CommandContainer cmd) {
		if (commands.containsKey(cmd.invoke.toLowerCase())) {
			boolean save = commands.get(cmd.invoke.toLowerCase()).allowExecute(cmd.args, cmd.event);
			
			if (save) {
				try {
					commands.get(cmd.invoke.toLowerCase()).action(cmd.args, cmd.event);
				} catch (Exception e) {
					LOG.warn("An exception while executing the command "+cmd.event.getMessage().getContentRaw(),e);
					save = false;
				}
			}
			commands.get(cmd.invoke.toLowerCase()).executed(save, cmd.event);
		} else {
			JDAUtils.msg(cmd.event.getChannel(), "Unknown Command");
		}
	}
}
