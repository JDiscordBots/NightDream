/*
 * Copyright (c) JDiscordBots 2019
 * File: SEval.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

@BotCommand("seval")
public class SEval extends Eval {
	private Timer timer;

	@Override
	protected void onSuccess(Object result, GuildMessageReceivedEvent event,long time) {
		// do nothing
	}
	
	@Override
	protected void onError(Throwable e, GuildMessageReceivedEvent event) {
		event.getChannel().sendMessage("No...").queue(msg->{
			msg.addReaction("\u274C").queue();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message message = event.getChannel().retrieveMessageById(msg.getId()).complete();
					for (MessageReaction reaction : message.getReactions()) {
						if("\u274C".equals(reaction.getReactionEmote().getEmoji()) && reaction.retrieveUsers().complete().contains(event.getAuthor())) {
							message.delete().queue();
							return;
						}
					}
				}
			}, 60000);
		});
	}
	@Override
	public String help() {
		return super.help()+", but silently";
	}
}
