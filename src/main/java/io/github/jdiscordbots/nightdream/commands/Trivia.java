/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Trivia.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.listeners.TriviaListener;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

@BotCommand("trivia")
public class Trivia implements Command{

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		String url="https://opentdb.com/api.php?amount=1";
		try(Scanner scan=new Scanner(new BufferedInputStream(new URL(url).openConnection().getInputStream()), StandardCharsets.UTF_8.toString())){
			JSONObject json=new JSONObject(scan.nextLine()).getJSONArray("results").getJSONObject(0);
			String correct=json.getString("correct_answer");
			JSONArray incorrect=json.getJSONArray("incorrect_answers");
			String[] answers=new String[incorrect.length()+1];
			for (int i = 0; i < incorrect.length(); i++) {
				answers[i]=incorrect.getString(i);
			}
			answers[answers.length-1]=correct;
			Arrays.sort(answers);
			EmbedBuilder builder=new EmbedBuilder();
			builder.setTitle("Trivia")
			.setColor(0x212121)
			.addField(json.getString("category"), json.getString("question"), false)
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
