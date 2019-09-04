/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: MsgLog.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("msglog")
public class MsgLog implements Command {
	
	private static final String NEED_MENTIONED_CHANNEL="<:IconProvide:553870022125027329> I need a mentioned channel";

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if (args.length == 0) {
			event.getChannel().sendMessage(NEED_MENTIONED_CHANNEL).queue();
			return;
		}
		if ("none".equals(args[0])) {
			BotData.resetMsgLogChannel(event.getGuild());
			event.getChannel().sendMessage("Removed").queue();
			return;
		}
		if (event.getMessage().getMentionedChannels().isEmpty()) {
			event.getChannel().sendMessage(NEED_MENTIONED_CHANNEL).queue();
			return;
		}
		TextChannel channel = event.getMessage().getMentionedChannels().get(0);
		if(channel==null) {
			event.getChannel().sendMessage(NEED_MENTIONED_CHANNEL).queue();
			return;
		}
		BotData.setMsgLogChannel(channel.getId(), event.getGuild());
		event.getChannel().sendMessage("Set! `" + BotData.getPrefix(event.getGuild()) + "msglog none` to disable.").queue();
	}

	@Override
	public String help() {
		return "Sets up a log channel for deleted messages";
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || JDAUtils.checkOwner(event);
	}
}
