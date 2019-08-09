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
			// return
		}
		else if(event.getMessage().getContentRaw().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getAsMention())&& !event.getAuthor().isBot()) {
			event.getTextChannel().sendMessage("My prefix here: `"+BotData.getPrefix(event.getGuild())+"`").complete();
		}
		else if (event.getMessage().getContentRaw().startsWith(event.getGuild().getMember(event.getJDA().getSelfUser()).getAsMention()+" ") && !event.getMessage().getAuthor().isBot()) {
			if(event.getMessage().getContentRaw().toLowerCase().endsWith("> i messed up")) {
				BotData.setPrefix(event.getGuild(), BotData.getDefaultPrefix());
				event.getTextChannel().sendMessage("It's fine :smiley:\nI reset the prefix on this guild.").complete();
			}else {
				CommandHandler.handleCommand(CommandParser.parser(event,event.getMessage().getContentRaw().split(" ")[0]+" "));
			}
		}
		else if (event.getMessage().getContentDisplay().startsWith(BotData.getPrefix(event.getGuild())) && (!event.getMessage().getAuthor().isBot())) {
			CommandHandler.handleCommand(CommandParser.parser(event));
		}
		
	}
}
