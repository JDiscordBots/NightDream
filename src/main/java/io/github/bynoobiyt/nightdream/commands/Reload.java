package io.github.bynoobiyt.nightdream.commands;

import java.awt.Color;

import io.github.bynoobiyt.nightdream.listeners.MsgLogListener;
import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

@BotCommand("reload")
public class Reload implements Command {

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event);
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		final TextChannel tc=event.getChannel();
		if(args.length==0) {
			JDAUtils.msg(event.getChannel(), "reloading all Properties...",Color.YELLOW);
			BotData.reloadAllProperties();
			JDAUtils.msg(event.getChannel(), "reloaded!");
			return;
		}
		switch(args[0]) {
		case "login":
		case "reconnect"://TODO fix console error(InterruptedException)
			try {
				JDAUtils.msg(event.getChannel(), "reconnecting...",Color.YELLOW);
				JDAImpl jda=((JDAImpl)event.getJDA());
				jda.setAutoReconnect(false);
				jda.getClient().socket.disconnect();
				jda.getClient().reconnect(false);
				jda.addEventListener(new ListenerAdapter() {
					@Override
					public void onReconnect(@NotNull ReconnectedEvent event) {
						JDAUtils.msg(tc, "reconnected!");
						jda.removeEventListener(this);
					}
				});
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				event.getJDA().setAutoReconnect(true);
			}
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
		return null;
	}

}
