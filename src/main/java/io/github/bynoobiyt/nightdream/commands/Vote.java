/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Vote.java
 * Project: NightDream
 * All rights reserved!
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

@BotCommand("vote")
public class Vote implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder().setTitle(String.format("Vote for %s", event.getJDA().getSelfUser().getName()))
				.setColor(Color.white).setDescription(String.format("[<3](https://discordbots.org/bot/%s/vote)", event.getJDA().getSelfUser().getId()));

		JDAUtils.msg(event.getChannel(), eb.build());
	}

	@Override
	public String help() {
		return "Vote for me! <3";
	}
}
