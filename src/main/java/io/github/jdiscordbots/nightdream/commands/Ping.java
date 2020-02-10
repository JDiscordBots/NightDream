/*
 * Copyright (c) JDiscordBots 2019
 * File: Ping.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import io.github.jdiscordbots.nightdream.util.IconChooser;

@BotCommand("ping")
public class Ping implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		event.getChannel().sendMessage(":stopwatch:").queue(msg->{
			long ms= getMilliSeconds(msg.getTimeCreated()) - getMilliSeconds(event.getMessage().getTimeCreated());
			event.getJDA().getRestPing().queue(ping->msg.editMessage(IconChooser.getArrowIcon(event.getChannel())+" Latency: " + ms + "ms. API Latency is " + ping + "ms").queue());
		});
	}
	private static long getMilliSeconds(OffsetDateTime time) {
		return time.atZoneSameInstant(ZoneId.of("Z")).toInstant().toEpochMilli();
	}

	@Override
	public String help() {
		return "Pings!";
	}

	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
