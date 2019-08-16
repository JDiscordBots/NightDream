package io.github.bynoobiyt.nightdream.commands;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import io.github.bynoobiyt.nightdream.listeners.TriviaListener;
import io.github.bynoobiyt.nightdream.util.GeneralUtils;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("trivia")
public class Trivia implements Command{

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		String url="https://opentdb.com/api.php?amount=1";
		try(Scanner scan=new Scanner(new URL(url).openConnection().getInputStream())){
			String json=scan.nextLine();
			String correct=GeneralUtils.getJSONString(json, "correct_answer");
			String incorrect=GeneralUtils.getMultipleJSONStrings(json, "incorrect_answers");
			String[] answers=Arrays.copyOf(incorrect.split(", "), incorrect.split(", ").length+1);
			answers[answers.length-1]=correct;
			Arrays.sort(answers);
			EmbedBuilder builder=new EmbedBuilder();
			builder.setTitle("Trivia")
			.setColor(0x212121)
			.addField(GeneralUtils.getJSONString(json, "category"), GeneralUtils.getJSONString(json, "question"), false)
			.addField("Choices:",String.join(", ", answers),false)
			.setFooter("Type your answer in this channel!");
			event.getChannel().sendMessage(builder.build()).complete();
			TriviaListener.addQuestion(event.getChannel(), correct);
		}catch (IOException e) {
			JDAUtils.errmsg(event.getChannel(), "<:IconX:553868311960748044> Errored while trying to connect to server.");
		}
	}

	@Override
	public String help() {
		return "Plays a game of trivia";
	}

}
