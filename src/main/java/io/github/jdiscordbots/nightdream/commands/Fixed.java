/*
 * Copyright (c) JDiscordBots 2019
 * File: Fixed.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BotCommand("fixed")
public class Fixed implements Command {

	private static final String DISABLED_INVALID_CHAN = "Fixed command is disabled. To enable it, please insert a valid channel id into NightDream.properties.";
	private static final Logger LOG=LoggerFactory.getLogger(Fixed.class);
	
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		args=Stream.of(args).collect(Collectors.joining(" ")).split("\\|");
		if(args.length<2) {
			event.getChannel().sendMessage("Syntax: `fixed <id>|Original Bug[|<additional information>]`").queue();
			return;
		}
		int bugID = 0;

		try {
			bugID = Integer.parseInt(args[0].trim());
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage("Please enter a correct number for the bug id!").queue();
			return;
		}
		if (bugID<0||BotData.getBugID() < bugID) {
			event.getChannel().sendMessage("This bug id is not valid!").queue();
			return;
		}
		String bugDescription = args[1];
		
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setFooter("Reported as fixed by " + event.getAuthor().getName()).setTitle("Fixed bug " + bugID)
				.addField("Original bug", bugDescription, false);
		if (args.length>2) {
			eb.addField("Additional comment", args[2], false);
		}

		event.getJDA().getShardManager().getTextChannelById(BotData.getFixedBugsChannel()).sendMessage(eb.build()).queue();
	}

	@Override
	public String help() {
		return "Reports a bug as fixed";
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		if(!JDAUtils.checkOwner(event,args!=null)) {
			return false;
		}
		try {
			if(event.getJDA().getShardManager().getTextChannelById(BotData.getFixedBugsChannel())==null) {
				if(args!=null) {
					LOG.warn(DISABLED_INVALID_CHAN);
				}
				return false;
			}
		} catch (IllegalArgumentException e) {
			if(args!=null) {
				LOG.warn(DISABLED_INVALID_CHAN);
			}
			return false;
		}
		return true;
	}
	
	@Override
    public String permNeeded() {
    	return "Bot-Admin";
    }
	
	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
