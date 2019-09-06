/*
 * Copyright (c) JDiscordBots 2019
 * File: Reload.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.listeners.MsgLogListener;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@BotCommand("reload")
public class Reload implements Command {

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		
		if(args.length==0) {
			JDAUtils.msg(event.getChannel(), "reloading all Properties...",Color.YELLOW);
			BotData.reloadAllProperties();
			JDAUtils.msg(event.getChannel(), "reloaded!");
			return;
		}
		switch(args[0]) {
		case "login":
		case "reconnect":
			JDAUtils.msg(event.getChannel(), "reconnecting...",Color.YELLOW);
			JDAImpl jda=((JDAImpl)event.getJDA());
			jda.getClient().close();
			final TextChannel tc=event.getChannel();
			jda.addEventListener(new ListenerAdapter() {
				@Override
				public void onReconnect(@NotNull ReconnectedEvent event) {
					JDAUtils.msg(tc, "reconnected!");
					jda.removeEventListener(this);
				}
			});
			break;
		case "props":
			JDAUtils.msg(event.getChannel(), "reloading all Properties...");
			BotData.reloadAllProperties();
			JDAUtils.msg(event.getChannel(), "reloaded!");
			break;
		case "guild":
			JDAUtils.msg(event.getChannel(), "reloading guild Properties...");
			BotData.reloadGuildProperties(event.getGuild());
			JDAUtils.msg(event.getChannel(), "reloaded guild Properties!");
			break;
		case "msgcache":
			for (Object obj : event.getJDA().getRegisteredListeners()) {
				if(obj instanceof MsgLogListener) {
					((MsgLogListener)obj).clearCache();
				}
			}
			break;
		default:
			JDAUtils.errmsg(event.getChannel(), "Invalid argument "+args[0]);
		}
	}

	@Override
	public String help() {
		return "reconnects (`reload login`/`reload reconnect`),\n"
				+ "reloads settings for the current guild(`reload guild`) "
				+ "or everything(`reload props`)\n"
				+ "or deletes the message cache";
	}

}
