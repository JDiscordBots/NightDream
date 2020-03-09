/*
 * Copyright (c) JDiscordBots 2019
 * File: NPM.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

@BotCommand("npm")
public class NPM implements Command{
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" I need a package name").queue();
			return;
		}
		String url="https://registry.yarnpkg.com/"+args[0];
		JSONObject jsonObj=GeneralUtils.getJSONFromURL(url);
		if(jsonObj==null) {
			event.getChannel().sendMessage("Are you sure the package exists?").queue();
		}else {
			JSONObject versions=jsonObj.getJSONObject("versions");
			Optional<String> infoHolder=versions.keySet().stream().max((a,b)->a.compareTo(b));
			if(!infoHolder.isPresent()) {
				JDAUtils.errmsg(event.getChannel(), "No version info provided");
				return;
			}
			JSONObject versionInfo=versions.getJSONObject(infoHolder.get());
			String keywordStr = "<nothing>";
			if(jsonObj.has("keywords")) {
				JSONArray keywords=jsonObj.getJSONArray("keywords");
				if(keywords.length()!=0) {
					keywordStr=keywords.join(", ");
				}
			}
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0xfb3b49)
			.setTitle("Result")
			.addField(new Field("name", "`"+jsonObj.getString("name")+"`", true))
			.addField(new Field("Description", jsonObj.getString("description"), true))
			.addField(new Field("Current Version", versionInfo.getString("version"), true))
			.addField(new Field("Keywords", "`"+keywordStr+"`", true))
			.addField(new Field("Author", "NPM says `"+jsonObj.getJSONArray("maintainers").getJSONObject(0).getString("name")+"` | package.json says "+versionInfo.getJSONObject("author").getString("name"), true));
			if(args[0].startsWith("@")&&args[0].contains("/")) {
				builder.addField(new Field("Scope", "`"+args[0].split("/")[0].substring(1)+"`", true));
			}
			event.getChannel().sendMessage(builder.build()).queue();
		}
	}
	@Override
	public String help() {
		return "Allows you to view info about a npm package";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
