/*
 * Copyright (c) JDiscordBots 2019
 * File: Photo.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@BotCommand("photo")
public class Photo implements Command {
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> Search Query, please.").queue();
			return;
		}
		if("".equals(BotData.getPixaBayAPIKey())) {
			JDAUtils.errmsg(event.getChannel(),"This command is disabled because there is no API Key set.");
			NDLogger.logWithModule(LogType.WARN, "Commands", "no Pixabay API Key provided");
			return;
		}
		event.getChannel().sendTyping();
		JSONObject json=null;
		try {
			json = GeneralUtils.getJSONFromURL("https://pixabay.com/api/?image_type=photo&key="+BotData.getPixaBayAPIKey()+"&q="+URLEncoder.encode(String.join(" ", args),StandardCharsets.UTF_8.name() ));
		} catch (UnsupportedEncodingException ignore) {
			//ignore
		}
		if(json==null) {
			event.getChannel().sendMessage("<:IconX:553868311960748044> Something went badly wrong - the server did not respond! Try again **in a few minutes**.").queue();
		}else {
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0x212121);
			JSONArray hits=json.getJSONArray("hits");
			if(hits.length()==0) {
				builder.setTitle("<:IconProvide:553870022125027329> Nothing found")
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
