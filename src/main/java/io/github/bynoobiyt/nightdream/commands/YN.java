/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: YN.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@BotCommand("yn")
public class YN implements Command {
	
	private static final Logger LOG=LoggerFactory.getLogger(YN.class);

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
			LOG.warn("IO Error while executing a yes-no query",e);
		}
	}

	@Override
	public String help() {
		return "Answer a yes/no question";
	}

}
