package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;

@BotCommand("calc")
public class Calc implements Command {


	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		String operation="";
		String wrongFormat = String.format("<:IconProvide:553870022125027329> Format: `%scalc num1 [+,-,*,/] num2`", BotData.getDefaultPrefix());
		if (args.length < 2) {
			event.getChannel().sendMessage(wrongFormat).queue();
			return;
		}
		try {
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
				default:
					event.getChannel().sendMessage(wrongFormat).queue();
					return;
			}
			EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle(String.format("%s %s %s", num1, operation, num2))
					.setDescription(String.valueOf(result));
			event.getTextChannel().sendMessage(eb.build()).queue();
        } catch (NumberFormatException e) {
	        event.getChannel().sendMessage(wrongFormat).queue();
        }
	}

	@Override
	public String help() {
		return "Does some calculation for you";
	}
}
