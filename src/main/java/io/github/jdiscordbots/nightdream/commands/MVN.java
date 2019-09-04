/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: MVN.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
		try(Scanner scan=new Scanner(new BufferedInputStream(new URL(url).openConnection().getInputStream()),StandardCharsets.UTF_8.name())){
			JSONArray docs=new JSONObject(scan.nextLine()).getJSONObject("response").getJSONArray("docs");
			if(docs.length()==0) {
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0xdc6328)
				.addField("<:IconProvide:553870022125027329> Nothing found", "Try something different.", false);
				event.getChannel().sendMessage(builder.build()).queue();
				return;
			}
			JSONObject json=docs.getJSONObject(0);
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0xdc6328)
			.setTitle("Result")
			.addField(new Field("Group ID", "`"+json.getString("g")+"`", true))
			.addField(new Field("Artifact ID", json.getString("a"), true))
			.addField(new Field("Current Version", json.getString("latestVersion"), true))
			.addField(new Field("Repository", json.getString("repositoryId"), true));
			
			JDAUtils.msg(event.getChannel(), builder.build());
		}catch (IOException e) {
			JDAUtils.errmsg(event.getChannel(), "An error occurred, maybe your query is invalid");
		}
	}
	@Override
	public String help() {
		return "Allows you to view info about a maven artifact";
	}
	
}
