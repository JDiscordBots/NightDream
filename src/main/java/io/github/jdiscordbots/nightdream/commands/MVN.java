/*
 * Copyright (c) JDiscordBots 2019
 * File: MVN.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONArray;
import org.json.JSONObject;

@BotCommand("mvn")
public class MVN implements Command{

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a package name").queue();
		}
		String url="http://search.maven.org/solrsearch/select?q="+args[0]+"&wt=json";
		JSONObject json=GeneralUtils.getJSONFromURL(url);
		if(json==null) {
			JDAUtils.errmsg(event.getChannel(), "An error occurred, maybe your query is invalid");
		}else {
			JSONArray docs=json.getJSONObject("response").getJSONArray("docs");
			if(docs.length()==0) {
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0xdc6328)
				.addField("<:IconProvide:553870022125027329> Nothing found", "Try something different.", false);
				event.getChannel().sendMessage(builder.build()).queue();
				return;
			}
			JSONObject data=docs.getJSONObject(0);
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0xdc6328)
			.setTitle("Result")
			.addField(new Field("Group ID", "`"+data.getString("g")+"`", true))
			.addField(new Field("Artifact ID", data.getString("a"), true))
			.addField(new Field("Current Version", data.getString("latestVersion"), true))
			.addField(new Field("Repository", data.getString("repositoryId"), true));
			
			JDAUtils.msg(event.getChannel(), builder.build());
		}
	}
	@Override
	public String help() {
		return "Allows you to view info about a maven artifact";
	}
	
	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
