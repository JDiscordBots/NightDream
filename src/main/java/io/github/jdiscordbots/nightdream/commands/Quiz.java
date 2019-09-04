/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Quiz.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.listeners.TriviaListener;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;

@BotCommand("quiz")
public class Quiz implements Command{

	private List<Map.Entry<String, String[]>> questions;
	public Quiz() {
		questions=new ArrayList<>();
		try(Scanner scan=new Scanner(Quiz.class.getClassLoader().getResourceAsStream("quiz.json"), StandardCharsets.UTF_8.name())){
			StringBuilder sb=new StringBuilder();
			while(scan.hasNext()) {
				sb.append(scan.nextLine());
			}
			JSONArray jsonQuestions=new JSONArray(sb.toString());
			for (int i = 0; i < jsonQuestions.length(); i++) {
				JSONObject jsonQuestion=jsonQuestions.getJSONObject(i);
				String question=jsonQuestion.getString("question");
				JSONArray jsonAnswers=jsonQuestion.getJSONArray("answers");
				String[] answers=new String[jsonAnswers.length()];
				for (int j = 0; j < answers.length; j++) {
					answers[j]=jsonAnswers.getString(j);
				}
				questions.add(new AbstractMap.SimpleEntry<>(question, answers));
			}
		}
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		Map.Entry<String, String[]> question=questions.get(GeneralUtils.getRandInt(questions.size()));
		event.getChannel().sendMessage("**Warning**: This command is very raw (about 6 questions). Use "+BotData.getPrefix(event.getGuild())+"trivia instead.\n\n"
				+ question.getKey()).queue();
		TriviaListener.addQuestion(event.getChannel(), question.getValue());
	}

	@Override
	public String help() {
		return "Plays a quiz-style game";
	}

}
