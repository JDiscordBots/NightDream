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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@BotCommand("help")
public class Help implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder builder=new EmbedBuilder().setColor(Color.white);
		builder.setFooter("Prefix in " + event.getGuild().getName() + ": " + BotData.getPrefix(event.getGuild()) + " | Release " + NightDream.VERSION);
		Map<String, Command> commands = CommandHandler.getCommands();
		if(args.length==0) {
			builder.setTitle("Nightdream Commands");
			showAll(builder, event, commands,s->true);
		}else {
			builder.setTitle("Nightdream Commands (Searching for " + String.join(", ", args) + ")");
			if (!(args.length == 1 && commands.containsKey(args[0])
					&& detailedHelp(builder, event, args[0], commands.get(args[0])))) {
				showAll(builder, event, commands, s->{
					for (String arg : args) {
						if(s.startsWith(arg)) {
							return true;
						}
					}
					return false;
				});
			}
		}
		event.getChannel().sendMessage(builder.build()).queue();
	}
	private static void showAll(EmbedBuilder builder, GuildMessageReceivedEvent event,Map<String, Command> commands,Predicate<String> filter) {
		AtomicBoolean found=new AtomicBoolean(false);
		final EnumMap<CommandType, StringBuilder> helpBuilders=new EnumMap<>(CommandType.class);
		commands.forEach((k,v)->{
			String help=v.help();
			if(help!=null&&filter.test(k)&&v.allowExecute(null, event)) {
				CommandType type=v.getType();
				if(!helpBuilders.containsKey(type)) {
					helpBuilders.put(type, new StringBuilder());
				}
				helpBuilders.get(type)
				.append('`')
				.append(k)
				.append('`')
				.append(" - ")
				.append(v.help())
				.append('\n');
				found.set(true);
			}
		});
		
		if(!found.get()) {
			builder.setDescription("Nothing found");
		}else {
			helpBuilders.forEach((k,v)->builder.addField(k.getDisplayName(), v.toString(), false));
		}
	}
	private static boolean detailedHelp(EmbedBuilder builder, GuildMessageReceivedEvent event,String name, Command cmd) {
		String help=cmd.help();
		if(help==null||!cmd.allowExecute(null, event)) {
			return false;
		}
		builder.setTitle("Help with "+name);
		builder.addField("Category",cmd.getType().getDisplayName(),true);
		builder.addField("Permissions needed",cmd.permNeeded(),true);
		builder.addField("Description",cmd.help(),true);
		return true;
	}
	@Override
	public String help() {
		return "¯\\_(ツ)_/¯";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
