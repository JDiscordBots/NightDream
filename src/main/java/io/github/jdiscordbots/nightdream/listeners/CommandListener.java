/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: CommandListener.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.listeners;

import io.github.jdiscordbots.nightdream.core.CommandHandler;
import io.github.jdiscordbots.nightdream.core.CommandParser;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
	public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
		if(event.getMessage().getContentRaw().equals(event.getGuild().getSelfMember().getAsMention())&& !event.getAuthor().isBot()) {
			event.getChannel().sendMessage("My prefix here: `"+BotData.getPrefix(event.getGuild())+"`").queue();
		}else if (event.getMessage().getContentRaw().startsWith(event.getGuild().getSelfMember().getAsMention()+" ") && !event.getMessage().getAuthor().isBot()) {
			if(event.getMessage().getContentRaw().toLowerCase().endsWith("> i messed up")) {
				BotData.setPrefix(event.getGuild(), BotData.getDefaultPrefix());
				event.getChannel().sendMessage("It's fine :smiley:\nI reset the prefix on this guild.").queue();
			}else {
				CommandHandler.handleCommand(CommandParser.parser(event,event.getMessage().getContentRaw().split(" ")[0]+" "));
			}
		}else if (event.getMessage().getContentDisplay().startsWith(BotData.getPrefix(event.getGuild())) && (!event.getMessage().getAuthor().isBot())) {
			CommandHandler.handleCommand(CommandParser.parser(event));
		}
		
	}
}
