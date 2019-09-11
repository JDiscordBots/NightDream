/*
 * Copyright (c) JDiscordBots 2019
 * File: Ping.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@BotCommand("ping")
public class Ping implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		Message msg = event.getChannel().sendMessage(":stopwatch:").complete();
		long ms= getMilliSeconds(msg.getTimeCreated()) - getMilliSeconds(event.getMessage().getTimeCreated());
		msg.editMessage("<:IconThis:553869005820002324> Latency: " + ms + "ms. API Latency is " + event.getJDA().getRestPing().complete() + "ms").queue();
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
