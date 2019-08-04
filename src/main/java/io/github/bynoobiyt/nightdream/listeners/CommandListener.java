package io.github.bynoobiyt.nightdream.listeners;

import io.github.bynoobiyt.nightdream.core.CommandHandler;
import io.github.bynoobiyt.nightdream.core.CommandParser;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
/**
 * Listener for Commands
 * @author Daniel Schmid
 */
@BotListener
public class CommandListener extends ListenerAdapter {
	/**
	 * if anyone sends a {@link Message} and this Message begins with the Bot prefix for the {@link Guild} it will be parsed and executed
	 * @see CommandParser
	 * @see CommandHandler
	 */
	@Override
	public void onMessageReceived(final MessageReceivedEvent event) {
		if (!event.isFromGuild()){
			return;
		}
		if (event.getMessage().getContentDisplay().startsWith("nd-prefix") && !event.getAuthor().isBot()) {
			CommandHandler.handleCommand(CommandParser.parser(event,"nd-"));
			return;
		}
		if ((event.getMessage().getMentionedUsers().size() == 1) && (event.getMessage().getContentDisplay().startsWith("@")) && event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()) && !event.getMessage().getAuthor().isBot()) {
			CommandHandler.handleCommand(CommandParser.parser(event,event.getMessage().getContentRaw().split(" ")[0]+" "));
		}
		if (event.getMessage().getContentDisplay().startsWith(BotData.getPrefix(event.getGuild())) && (!event.getMessage().getAuthor().isBot())) {
			CommandHandler.handleCommand(CommandParser.parser(event));
		}
		
	}
}
