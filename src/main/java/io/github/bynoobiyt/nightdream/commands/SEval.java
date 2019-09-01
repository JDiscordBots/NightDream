/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: SEval.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.bynoobiyt.nightdream.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

@BotCommand("seval")
public class SEval extends Eval {

	@Override
	protected void onSuccess(Object result, GuildMessageReceivedEvent event) {
		// do nothing
	}
	
	@Override
	protected void onError(Exception e, GuildMessageReceivedEvent event) {
		final Message msg = event.getChannel().sendMessage("No...").complete();
		msg.addReaction("\u274C").queue();
		new Timer().schedule(new TimerTask() {
			
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
	}

}
