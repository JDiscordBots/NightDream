package io.github.bynoobiyt.nightdream.commands;

import java.awt.Color;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;

@BotCommand("reload")
public class Reload implements Command {

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return Utils.checkOwner(event);
	}
	
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		final TextChannel tc=event.getTextChannel();
		if(args.length==0) {
			Utils.msg(event.getTextChannel(), "reloading all Properties...",Color.YELLOW,false);
			BotData.reloadAllProperties();
			Utils.msg(event.getTextChannel(), "reloaded!");
			return;
		}
		switch(args[0]) {
		case "login":
		case "reconnect"://TODO fix console error(InterruptedException)
			try {
				Utils.msg(event.getTextChannel(), "reconnecting...",Color.YELLOW,false);
				JDAImpl jda=((JDAImpl)event.getJDA());
				jda.setAutoReconnect(false);
				jda.getClient().socket.disconnect();
				jda.getClient().reconnect(false);
				jda.addEventListener(new ListenerAdapter() {
					@Override
					public void onReconnect(ReconnectedEvent event) {
						Utils.msg(tc, "reconnected!");
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
			Utils.msg(event.getTextChannel(), "reloading all Properties...");
			BotData.reloadAllProperties();
			Utils.msg(event.getTextChannel(), "reloaded!");
			break;
		case "guild":
			Utils.msg(event.getTextChannel(), "reloading all Properties...");
			BotData.reloadGuildProperties(event.getGuild());
			Utils.msg(event.getTextChannel(), "reloaded!");
			break;
		default:
			Utils.errmsg(event.getTextChannel(), "Invalid argument "+args[0]);
		}
		
			
	}

	@Override
	public String help() {
		return null;
	}

}
