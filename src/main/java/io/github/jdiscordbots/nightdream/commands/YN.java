/*
 * Copyright (c) JDiscordBots 2019
 * File: YN.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@BotCommand("yn")
public class YN implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		try(Scanner scan=new Scanner(new BufferedInputStream(new URL("https://yesno.wtf/api").openConnection().getInputStream()), StandardCharsets.UTF_8.toString())){
			JSONObject json=new JSONObject(scan.nextLine());
			
			String answer=json.getString("answer")+"!";
			answer=Character.toUpperCase(answer.charAt(0))+answer.substring(1);
			String url=json.getString("image");
			event.getChannel().sendMessage(
					new EmbedBuilder()
					.setColor(0x212121)
					.setTitle(answer)
					.setImage(url)
					.build()).queue();
		} catch (IOException e) {
			JDAUtils.errmsg(event.getChannel(), "something went wrong.");
			NDLogger.logWithoutModule(LogType.ERROR, "IO Error while executing a yes-no query", e);
		}
	}

	@Override
	public String help() {
		return "Answer a yes/no question";
	}

	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}
}
