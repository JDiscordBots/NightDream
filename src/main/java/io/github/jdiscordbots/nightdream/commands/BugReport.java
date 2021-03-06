/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: BugReport.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BotCommand("bugreport")
public class BugReport implements Command {

	private static final String DISABLED_INVALID_CHAN="Bug report command is disabled. To enable it, please insert a valid channel id into NightDream.properties.";
	private static final Logger LOG=LoggerFactory.getLogger(BugReport.class);
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("Please provide a message for the bugreport.").queue();
			return;
		}
		int latestBugId = BotData.getBugID();

		int thisId = latestBugId + 1;

		BotData.setBugID(thisId);
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("New Bug").setDescription(String.join(" ", args))
				.setFooter(event.getAuthor().getAsTag() + " | Bug ID " + thisId);

		event.getJDA().getShardManager().getTextChannelById(BotData.getBugReportChannel()).sendMessage(eb.build()).queue();

		event.getChannel().sendMessage("Sent with ID " + thisId).queue();
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		if (BotData.getBugReportChannel() == null||"".equals(BotData.getBugReportChannel())) {
			BotData.setBugReportChannel("");
			if(args!=null) {
				LOG.warn(DISABLED_INVALID_CHAN);
			}
			return false;
		}
		try {
			if(event.getJDA().getShardManager().getTextChannelById(BotData.getBugReportChannel())==null) {
				if(args!=null) {
					LOG.warn(DISABLED_INVALID_CHAN);
				}
				return false;
			}
		} catch (NumberFormatException e) {
			if(args!=null) {
				LOG.warn(DISABLED_INVALID_CHAN);
			}
			return false;
		}
		if("banned".equals(BotData.STORAGE.read("bugs", event.getAuthor().getId(),""))) {
			event.getChannel().sendMessage(IconChooser.getInfoIcon(event.getChannel())+" You were banned from reporting bugs. If you believe this should change, contact the instance owner (see `"+BotData.getPrefix(event.getGuild())+"info`)").queue();
			return false;
		}
		return true;
	}

	@Override
	public String help() {
		return "Files a bug report";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
