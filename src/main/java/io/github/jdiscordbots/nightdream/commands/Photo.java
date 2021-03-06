/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Photo.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@BotCommand("photo")
public class Photo implements Command {
	
	private static final Logger LOG=LoggerFactory.getLogger(Photo.class);
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" Search Query, please.").queue();
			return;
		}
		if("".equals(BotData.getPixaBayAPIKey())) {
			JDAUtils.errmsg(event.getChannel(),"This command is disabled because there is no API Key set.");
			LOG.warn("no Pixabay API Key provided");
			return;
		}
		event.getChannel().sendTyping().queue();
		JSONObject json=null;
		try {
			json = GeneralUtils.getJSONFromURL("https://pixabay.com/api/?image_type=photo&key="+BotData.getPixaBayAPIKey()+"&q="+URLEncoder.encode(String.join(" ", args),StandardCharsets.UTF_8.name() ));
		} catch (UnsupportedEncodingException ignore) {
			//ignore
		}
		if(json==null) {
			event.getChannel().sendMessage(IconChooser.getErrorIcon(event.getChannel())+" Something went badly wrong - the server did not respond! Try again **in a few minutes**.").queue();
		}else {
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0x212121);
			JSONArray hits=json.getJSONArray("hits");
			if(hits.length()==0) {
				builder.setTitle(IconChooser.getQuestionIcon(event.getChannel())+" Nothing found")
                .setDescription("Try something different.");
			}else {
				String imgUrl=hits.getJSONObject(0).getString("largeImageURL");
				builder.setFooter("Results from Pixabay [https://pixabay.com]")
                .setTitle("Result")
                .setImage(imgUrl);
			}
			
			event.getChannel().sendMessage(builder.build()).queue();
		}
	}

	@Override
	public String help() {
		return "Gets a photo from Pixabay";
	}

	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}
}
