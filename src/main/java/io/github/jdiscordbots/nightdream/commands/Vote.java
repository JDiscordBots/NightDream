/*
 * Copyright (c) JDiscordBots 2019
 * File: Vote.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@BotCommand("vote")
public class Vote implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder().setTitle(String.format("Vote for %s", event.getJDA().getSelfUser().getName()))
				.setColor(Color.white).setDescription(String.format("[<3](https://top.gg/bot/%s/vote)", event.getJDA().getSelfUser().getId()));
		JDAUtils.msg(event.getChannel(), eb.build());
	}

	@Override
	public String help() {
		return "Vote for me! <3";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
