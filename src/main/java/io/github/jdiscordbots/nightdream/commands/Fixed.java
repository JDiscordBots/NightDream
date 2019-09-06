/*
 * Copyright (c) JDiscordBots 2019
 * File: Fixed.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@BotCommand("fixed")
public class Fixed implements Command {

	private static final String DISABLED_INVALID_CHAN = "Fixed command is disabled. To enable it, please insert a valid channel id into NightDream.properties.";
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string).append(' ');
		}
		args = sb.toString().split("\\|");

		int bugID = 0;
		

		try {
			bugID = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			JDAUtils.errmsg(event.getChannel(), "Please enter a correct number for the bug id!");
		}
		if (BotData.getBugID() < bugID) {
			event.getChannel().sendMessage("This bug id is not valid!").queue();
			return;
		}
		String bugDescription = args[1];
		String comment = args[2];
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setFooter("Reported as fixed by " + event.getAuthor().getName()).setTitle("Fixed bug " + bugID)
				.addField("Original bug", bugDescription, false);
		if (comment != null) {
			eb.addField("Additional comment", comment, false);
		}

		event.getJDA().getTextChannelById(BotData.getFixedBugsChannel()).sendMessage(eb.build()).queue();
	}

	@Override
	public String help() {
		return "Reports a bug as fixed";
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		if (BotData.getFixedBugsChannel() == null||"".equals(BotData.getBugReportChannel())) {
			BotData.setFixedBugsChannel("");
			NDLogger.logWithoutModule(LogType.WARN, DISABLED_INVALID_CHAN);
			return false;
		}
		try {
			if(event.getJDA().getTextChannelById(BotData.getFixedBugsChannel())==null) {
				NDLogger.logWithModule(LogType.WARN, "Commands",  DISABLED_INVALID_CHAN);
				return false;
			}
		} catch (NumberFormatException e) {
			NDLogger.logWithoutModule(LogType.WARN, DISABLED_INVALID_CHAN);
			return false;
		}
		return JDAUtils.checkOwner(event);
	}
}
