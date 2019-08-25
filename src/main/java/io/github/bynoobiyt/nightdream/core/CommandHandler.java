package io.github.bynoobiyt.nightdream.core;

import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bynoobiyt.nightdream.commands.Command;
import io.github.bynoobiyt.nightdream.core.CommandParser.CommandContainer;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * executed by a listener when Message sent which begins with the Bot prefix
 * @author Daniel Schmid
 */
public class CommandHandler {
	private static final Map<String, Command> commands = new HashMap<>();
	
	private static final Logger LOG=LoggerFactory.getLogger(CommandHandler.class);
	
	private static final LevenshteinDistance distCalculator=LevenshteinDistance.getDefaultInstance();
	
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
				} catch (RuntimeException e) {
					LOG.warn("An exception while executing the command "+cmd.event.getMessage().getContentRaw(),e);
					save = false;
				}
			}
			commands.get(cmd.invoke.toLowerCase()).executed(save, cmd.event);
		} else {
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0x212121);
			Set<String> candidates = commands.keySet(); // populate this with lots of values
			Map<String, Integer> cache = new ConcurrentHashMap<>();
			Optional<String> min = candidates.parallelStream()
			    .map(String::trim)
			    .filter(s -> !s.equalsIgnoreCase(cmd.invoke))
			    .min((a, b) -> Integer.compare(
			      cache.computeIfAbsent(a, k -> distCalculator.apply(cmd.invoke, k)),
			      cache.computeIfAbsent(b, k -> distCalculator.apply(cmd.invoke, k))));
			String fieldText;
			if(min.isPresent()) {
				fieldText="Did you mean `"+BotData.getPrefix(cmd.event.getGuild())+min.get()+"`?";
			}else {
				fieldText="Try `"+BotData.getPrefix(cmd.event.getGuild())+"help` for a List of Commands";
			}
			builder.addField("<:IconProvide:553870022125027329> It seems that this command does not exist",
					fieldText, false);
			cmd.event.getChannel().sendMessage(builder.build()).queue();
		}
	}
}
