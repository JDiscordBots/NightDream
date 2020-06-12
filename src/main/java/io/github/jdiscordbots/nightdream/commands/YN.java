/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: YN.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONObject;

@BotCommand("yn")
public class YN implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		JSONObject json = GeneralUtils.getJSONFromURL("https://yesno.wtf/api");
		if (json == null) {
			JDAUtils.errmsg(event.getChannel(), "something went wrong.");
		} else {
			String answer = json.getString("answer") + "!";
			answer = Character.toUpperCase(answer.charAt(0)) + answer.substring(1);
			String url = json.getString("image");
			event.getChannel().sendMessage(new EmbedBuilder().setColor(0x212121).setTitle(answer).setImage(url).build()).queue();
		}
	}

	@Override
	public String help() {
		return "Answer a yes/no question";
	}

	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}
}
