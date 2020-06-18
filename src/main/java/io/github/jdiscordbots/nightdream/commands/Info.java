/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Info.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.core.NightDream;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.TextToGraphics;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Year;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BotCommand("info")
public class Info implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		final JDA jda = event.getJDA();
		String send = String.format(
				"Bot Info\n" + "\tIn %s guilds, serving %s users.\n" + "\tThis instance is owned by %s.\n"
						+ "\tJDA v%s\n" + "\tLogo Font: Avenir Next LT Pro / (c) Linotype\n"
						+ "\t(c) dan1st and Gehasstes %s, Version %s.\n "
						+ "\tThis is a copy of Daydream (https://gitlab.com/botstudio/daydream/) by SP46",
				event.getJDA().getGuilds().size(),
				jda.getGuilds().parallelStream().mapToInt(Guild::getMemberCount).sum(), Stream.of(BotData.getAdminIDs())
						.map(id -> jda.retrieveUserById(id).complete().getAsTag()).collect(Collectors.joining(" and ")),
				JDAInfo.VERSION, Year.now().getValue(), NightDream.VERSION);
		if (event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_ATTACH_FILES)) {
			TextToGraphics.sendTextAsImage(event.getChannel(), "info", send, event.getAuthor().getAsMention());
		} else {
			event.getChannel().sendMessage(send).queue();
		}
	}

	@Override
	public String help() {
		return "Displays bot information";
	}

	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
