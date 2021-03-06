/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Calc.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

@BotCommand({"calc","math"})
public class Calc implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		String wrongFormat = String.format("%s Format: `%scalc num1 [+,-,*,/,%%,**,root] num2`",IconChooser.getQuestionIcon(event.getChannel()), BotData.getPrefix(event.getGuild()));
		if (args.length <= 2) {
			event.getChannel().sendMessage(wrongFormat).queue();
			return;
		}
		try {
			String operation="";
			double num1 = Float.parseFloat(args[0]);
			double num2 = Float.parseFloat(args[2]);
			String operator = args[1];
			double result;
			switch (operator) {
				case "+":
					result = num1 + num2;
					operation = "plus";
					break;
				case "-":
					result = num1 - num2;
					operation = "minus";
					break;
				case "*":
					result = num1 * num2;
					operation = "by";
					break;
				case "/":
					result = num1 / num2;
					operation = "divided by";
					break;
				case "%":
					result = num1 % num2;
					operation = "mod";
					break;
				case "**":
					result = Math.pow(num1, num2);
					operation = "exponented by";
					break;
				case "root":
					result = Math.pow(num2, 1/num1);
					operation = "root of";
					break;
				default:
					event.getChannel().sendMessage(wrongFormat).queue();
					return;
			}
			EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle(String.format("%s %s %s", num1, operation, num2))
					.setDescription(String.valueOf(result));
			event.getChannel().sendMessage(eb.build()).queue();
        } catch (NumberFormatException e) {
	        event.getChannel().sendMessage(wrongFormat).queue();
        }
	}

	@Override
	public String help() {
		return "Does some calculation for you";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
