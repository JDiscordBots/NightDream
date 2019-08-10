package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@BotCommand("calc")
public class Calc implements Command {

	private String operation;
	private float result;
	private TextChannel channel;
	private float num1;
	private float num2;

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		List<String> operations = Arrays.asList("+", "-", "*", "/");

		channel = event.getTextChannel();
		String wrongFormat = String.format("<:IconProvide:553870022125027329> Format: `%scalc num1 [+,-,*,/] num2`", BotData.getDefaultPrefix());
		if (args.length == 0 || !operations.contains(args[1]) || args[2] == null) {
			event.getChannel().sendMessage(wrongFormat).queue();
			return;
		}
		try {
			Integer.parseInt(args[0]);
			Integer.parseInt(args[2]);
		} catch (Exception e) {
			event.getChannel().sendMessage(wrongFormat).queue();
			return;
		}
		String operator = args[1];

		num1 = Float.parseFloat(args[0]);
		num2 = Float.parseFloat(args[2]);

		switch (operator) {
			case "+":
				result = num1 + num2;
				operation = "plus";
				make(result, operation);
				break;
			case "-":
				result = num1 - num2;
				operation = "minus";
				make(result, operation);
				break;
			case "*":
				result = num1 * num2;
				operation = "by";
				make(result, operation);
				break;
			case "/":
				result = num1 / num2;
				operation = "divided by";
				make(result, operation);
				break;
			default:
				event.getChannel().sendMessage(wrongFormat).queue();
				break;
		}
	}

	private void make(float result, String operation) {
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle(String.format("%s %s %s", num1, operation, num2))
				.setDescription(String.valueOf(result));

		channel.sendMessage(eb.build()).queue();
	}

	@Override
	public String help() {
		return "Does some calculation for you";
	}
}
