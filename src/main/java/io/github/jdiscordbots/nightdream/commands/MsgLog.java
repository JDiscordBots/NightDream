/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: MsgLog.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("msglog")
public class MsgLog implements Command {

	private void sendNeedMentionedChannelMessage(TextChannel tc) {
		tc.sendMessage(IconChooser.getInfoIcon(tc) + " I need a mentioned channel").queue();
	}

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if (args.length == 0) {
			sendNeedMentionedChannelMessage(event.getChannel());
			return;
		}
		if ("none".equals(args[0])) {
			BotData.resetMsgLogChannel(event.getGuild());
			event.getChannel().sendMessage("Removed").queue();
			return;
		}
		if (event.getMessage().getMentionedChannels().isEmpty()) {
			sendNeedMentionedChannelMessage(event.getChannel());
			return;
		}
		TextChannel channel = event.getMessage().getMentionedChannels().get(0);
		if (channel.getGuild() != event.getGuild()) {
			event.getChannel().sendMessage(IconChooser.getErrorIcon(event.getChannel())+" You can only set msglog channels in the same server.").queue();
			return;
		}
		BotData.setMsgLogChannel(channel.getId(), event.getGuild());
		event.getChannel().sendMessage("Set! `" + BotData.getPrefix(event.getGuild()) + "msglog none` to disable.")
				.queue();
	}

	@Override
	public String help() {
		return "Sets up a log channel for deleted messages";
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		boolean allow = event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || JDAUtils.checkOwner(event, false);
		if (!allow) {
			event.getChannel()
					.sendMessage("This command requires the " + Permission.MESSAGE_MANAGE.getName() + " permission.")
					.queue();
		}
		return allow;
	}

	@Override
	public String permNeeded() {
		return Permission.MESSAGE_MANAGE.getName();
	}

	@Override
	public CommandType getType() {
		return CommandType.CONFIG;
	}
}
