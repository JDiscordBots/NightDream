/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Dns.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */
package io.github.jdiscordbots.nightdream.commands;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("dns")
public class Dns implements Command {
	private static final String UNKNOWN_MSG="<unknown>";
	private MessageEmbed parse(JSONObject json) {
		JSONObject qObj = json.getJSONArray("Question").getJSONObject(0);
		String question = qObj.getString("name");
		question=question.substring(0, question.length()-1);
		EmbedBuilder eb=new EmbedBuilder().setColor(0x212121)
				.setTitle(question+" (type "+qObj.getInt("type")+") resolves to:");
		JSONArray authority=json.getJSONArray("Authority");
		if(authority.length()>0) {
			JSONObject auth=authority.getJSONObject(0);
			eb.addField("Type", String.valueOf(auth.getInt("type")),false);
			eb.addField("Time to live (TTL)", String.valueOf(auth.getInt("TTL")), false);
			
			String[] data=auth.getString("data").split(Pattern.quote(". "));
			eb.addField("Nameserver", data[0], false);
			eb.addField("DNS Hostmaster", data[1], false);
			eb.addField("Mystery Text", data[2], false);
		}else {
			eb.addField("Type", UNKNOWN_MSG, false);
			eb.addField("Time to live (TTL)", UNKNOWN_MSG, false);
			eb.addField("Nameserver", UNKNOWN_MSG, false);
			eb.addField("DNS Hostmaster", UNKNOWN_MSG, false);
			eb.addField("Mystery Text", UNKNOWN_MSG, false);
		}
		if(json.has("Comment")) {
			eb.setFooter(json.getString("Comment"));
		}
		return eb.build();
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage(IconChooser.getInfoIcon(event.getChannel())+" Unknown Resolve Target").queue();
		}else {
			JSONObject json=GeneralUtils.getJSONFromURL("https://dns.google.com/resolve?type=PTR&name="+String.join(" ", args));
			if(json==null) {
				event.getChannel().sendMessage("Unknown Error").queue();
			}else {
				if(json.getInt("Status")==0) {
					event.getChannel().sendMessage(parse(json)).queue();
				}else {
					event.getChannel().sendMessage("Your query is unresolvable.\nTry with a different one?").queue();
				}
			}
		}
	}

	@Override
	public String help() {
		return "Resolve an address";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}

}
