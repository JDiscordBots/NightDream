/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Trivia.java
 * Project: NightDream
 * Licenced under GNU GPL!
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.listeners.TriviaListener;
import io.github.bynoobiyt.nightdream.util.GeneralUtils;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

@BotCommand("trivia")
public class Trivia implements Command{

	private static final Pattern MULTIPLE_SPLITTER=Pattern.compile(", ");
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		String url="https://opentdb.com/api.php?amount=1";
		try(Scanner scan=new Scanner(new BufferedInputStream(new URL(url).openConnection().getInputStream()), StandardCharsets.UTF_8.toString())){
			String json=scan.nextLine();
			String correct=GeneralUtils.getJSONString(json, "correct_answer");
			String incorrect=GeneralUtils.getMultipleJSONStrings(json, "incorrect_answers");
			String[] answers=Arrays.copyOf(MULTIPLE_SPLITTER.split(incorrect), MULTIPLE_SPLITTER.split(incorrect).length+1);
			answers[answers.length-1]=correct;
			Arrays.sort(answers);
			EmbedBuilder builder=new EmbedBuilder();
			builder.setTitle("Trivia")
			.setColor(0x212121)
			.addField(GeneralUtils.getJSONString(json, "category"), GeneralUtils.getJSONString(json, "question"), false)
			.addField("Choices:",String.join(", ", answers),false)
			.setFooter("Type your answer in this channel!");
			event.getChannel().sendMessage(builder.build()).queue();
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
