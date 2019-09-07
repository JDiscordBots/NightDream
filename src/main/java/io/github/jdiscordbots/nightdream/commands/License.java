/*
 * Copyright (c) JDiscordBots 2019
 * File: License.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("license")
public class License implements Command{
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length<1) {
			event.getChannel().sendMessage("I need a License.").queue();
			return;
		}
		if("advice".equalsIgnoreCase(args[0])) {
			event.getChannel().sendMessage("<https://developer.github.com/v3/licenses/> states:\n>>> "
		            +"GitHub is a lot of things, but it’s not a law firm. As such, GitHub does not provide legal advice. Using the Licenses API or sending us an email about it does not constitute legal advice nor does it create an attorney-client relationship. If you have any questions about what you can and can't do with a particular license, you should consult with your own legal counsel before moving forward. In fact, you should always consult with your own lawyer before making any decisions that might have legal ramifications or that may impact your legal rights.\n\n"
		            +"GitHub created the License API to help users get information about open source licenses and the projects that use them. We hope it helps, but please keep in mind that we’re not lawyers (at least not most of us aren't) and that we make mistakes like everyone else. For that reason, GitHub provides the API on an \"as-is\" basis and makes no warranties regarding any information or licenses provided on or through it, and disclaims liability for damages resulting from using the API.")
			.queue();
			return;
		}
		try(BufferedReader reader=new BufferedReader(new InputStreamReader(new URL("https://api.github.com/licenses/"+args[0]).openStream(),StandardCharsets.UTF_8))){
			StringBuilder sb=new StringBuilder();
			String line;
			while((line=reader.readLine())!=null) {
				sb.append(line);
			}
			JSONObject json=new JSONObject(sb.toString());
			EmbedBuilder builder=new EmbedBuilder();
			builder.setTitle(json.getString("name"))
			.setDescription(json.getString("description"));
			addFieldWithJSONArrayInJSONObject(builder, json, "permissions", "Permissions");
			addFieldWithJSONArrayInJSONObject(builder, json, "conditions", "Conditions");
			addFieldWithJSONArrayInJSONObject(builder, json, "limitations", "Limitations");
			builder.addField("Common?", json.getBoolean("featured")?"Yes":"No", true)
			.setColor(0x212121)
			.setFooter("Not legal advice - see `license advice`");
			event.getChannel().sendMessage(builder.build()).queue();
		}catch (FileNotFoundException e) {
			event.getChannel().sendMessage("No such license! Use the SPDX ID.").queue();
		} catch (IOException e) {
			event.getChannel().sendMessage("An Error occured - Please try again later").queue();
			NDLogger.logWithModule(LogType.WARN, "Commands", String.format("Cannot load licence %s: %s", args[0], e.getClass().getName()));
			
		}
	}
	private static void fillStringBuilderWithJSONArray(StringBuilder sb,JSONArray arr) {
		if(arr.length()==0) {
			sb.append("\n<nothing>");
		}else {
			for (Object object : arr) {
				sb.append('\n').append(object);
			}
		}
		
	}
	private static void addFieldWithJSONArrayInJSONObject(EmbedBuilder builder,JSONObject json,String jsonName,String fieldName) {
		StringBuilder sb=new StringBuilder();
		fillStringBuilderWithJSONArray(sb,json.getJSONArray(jsonName));
		builder.addField(fieldName,sb.toString(),true);
	}
	@Override
	public String help() {
		return "Get info about a license";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
