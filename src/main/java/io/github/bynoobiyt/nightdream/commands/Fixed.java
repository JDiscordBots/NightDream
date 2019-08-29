/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Fixed.java
 * Project: NightDream
 * Licenced under GNU GPL!
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

@BotCommand("fixed")
public class Fixed implements Command {

	private static final Logger LOG=LoggerFactory.getLogger(Fixed.class);
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
		return null;
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		if (BotData.getFixedBugsChannel() == null) {
			BotData.setFixedBugsChannel("");
			LOG.warn(DISABLED_INVALID_CHAN);
			return false;
		}
		try {
			if(event.getJDA().getTextChannelById(BotData.getFixedBugsChannel())==null) {
				LOG.warn(DISABLED_INVALID_CHAN);
			}
		} catch (NumberFormatException e) {
			LOG.warn(DISABLED_INVALID_CHAN);
			return false;
		}
		return JDAUtils.checkOwner(event);
	}
}
