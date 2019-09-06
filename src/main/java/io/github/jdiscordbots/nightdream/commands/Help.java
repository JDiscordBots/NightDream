/*
 * Copyright (c) JDiscordBots 2019
 * File: Help.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.core.CommandHandler;
import io.github.jdiscordbots.nightdream.core.NightDream;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@BotCommand("help")
public class Help implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder builder=new EmbedBuilder().setColor(Color.white);
		builder.setFooter("Prefix in " + event.getGuild().getName() + ": " + BotData.getPrefix(event.getGuild()) + " | Release " + NightDream.VERSION);
		Map<String, Command> commands = CommandHandler.getCommands();
		if(args.length==0) {
			builder.setTitle("Nightdream Commands");
			commands.forEach((k,v)->showHelp(builder, event, k, v));
		}else {
			Set<Command> commandsShown=new HashSet<>();
			builder.setTitle("Nightdream Commands (Searching for " + String.join(", ", args) + ")");
			boolean found=false;
			for (String cmdName : args) {
				boolean success=showResults(builder, event, commands, commandsShown, cmdName);
				if(!found) {
					found=success;
				}
			}
			if(!found) {
				builder.setDescription("Nothing found");
			}
		}
		event.getChannel().sendMessage(builder.build()).queue();
	}
	private boolean showResults(EmbedBuilder builder, GuildMessageReceivedEvent event,Map<String, Command> commands,Set<Command> commandsShown,String cmdName) {
		Command cmd=commands.get(cmdName);
		if(cmd!=null) {
			if(showHelp(builder, event, cmdName, cmd)) {
				return true;
			}
		}
		AtomicBoolean success=new AtomicBoolean();
		commands.forEach((k,v)->{
			if(k.startsWith(cmdName)&&!commandsShown.contains(v)) {
				showHelp(builder, event, k, v);
				commandsShown.add(v);
				success.set(true);
			}
		});
		return success.get();
	}
	private boolean showHelp(EmbedBuilder builder, GuildMessageReceivedEvent event,String name, Command cmd) {
		if(builder.getFields().size()>=25) {
			event.getChannel().sendMessage(builder.build()).queue();
			builder.getFields().clear();
		}
		if(cmd.allowExecute(null, event)) {
			String help=cmd.help();
			if(help!=null) {
				builder.addField(new Field(name, cmd.help(), true));
				return true;
			}
		}
		return false;
	}
	@Override
	public String help() {
		return "¯\\_(ツ)_/¯";
	}
}
