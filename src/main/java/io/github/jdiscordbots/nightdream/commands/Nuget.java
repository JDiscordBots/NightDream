/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Nuget.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("nuget")
public class Nuget implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" I need a package name").queue();
			return;
		}
		String url="https://azuresearch-usnc.nuget.org/query?q="+args[0]+"&take=1"+args[0];
		JSONObject jsonObj=GeneralUtils.getJSONFromURL(url);
		if(jsonObj==null) {
			event.getChannel().sendMessage("This didn't work...").queue();
		}else {
			if(jsonObj.getInt("totalHits")>0) {
				JSONObject data=jsonObj.getJSONArray("data").getJSONObject(0);
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0x004980);
				builder.setTitle("Result");
				builder.addField("Name",data.getString("title"),true);
				builder.addField("Description", data.getString("description"), true);
				builder.addField("Namespace", "`"+data.getString("id")+"`", true);
				builder.addField("Current Version", data.getString("version"), true);
				builder.addField("Authors", data.getJSONArray("authors").join(", "), true);
				
				JSONArray tags=data.getJSONArray("tags");
				if(tags.length()>0) {
					builder.addField("Tags", "`"+tags.join(", ")+"`", true);
				}
				builder.addField("Verified", String.valueOf(data.getBoolean("verified")), true);
				builder.addField("Downloads", String.valueOf(data.getInt("totalDownloads")), true);
				if(data.has("iconUrl")) {
					builder.setThumbnail(data.getString("iconUrl"));
				}
				event.getChannel().sendMessage(builder.build()).queue();
			}else {
				event.getChannel().sendMessage(
					new EmbedBuilder()
					.setColor(0x004980)
					.setTitle(IconChooser.getInfoIcon(event.getChannel())+" Nothing found")
					.setDescription("Try something different.").build()
				).queue();
			}
		}
	}

	@Override
	public String help() {
		return "Get Nuget package info (mvn is better)";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}

}
