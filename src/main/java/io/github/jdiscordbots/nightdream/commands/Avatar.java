/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Avatar.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

@BotCommand("avatar")
public class Avatar implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		User user;
		if(event.getMessage().getMentionedMembers().isEmpty()) {
			user = event.getAuthor();
		}else {
			user = event.getMessage().getMentionedMembers().get(0).getUser();
		}
		if(user.getAvatarUrl()==null) {
			event.getChannel().sendMessage("You don't have an avatar...").queue();
		}else {
			EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setImage(user.getAvatarUrl() + "?size=2048");
			event.getChannel().sendMessage(eb.build()).queue();
		}
	}

	@Override
	public String help() {
		return "Shows your (or someone else's) Avatar";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
