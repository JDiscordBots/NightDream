/*
 * Copyright (c) JDiscordBots 2020
 * File: Bio.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

import java.net.URLEncoder;

@BotCommand("bio")
public class Bio implements Command {

	private static final String BASE_URL = "https://api.discord.bio/v1/";
	private static final String DISCORD_CDN_AVATARS_BASE_URL = "https://cdn.discordapp.com/avatars/";


	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		final TextChannel channel = event.getChannel();
		final String error = IconChooser.getErrorIcon(channel) + " Something went wrong.\n\n" +
				"This command is in beta for now, this means among other things that bugs are not automatically reported.\n" +
				"Please use `" + BotData.getPrefix(event.getGuild()) + "bugreport` to report the bug manually with with the exact input provided.";
		final String missingArgs = IconChooser.getErrorIcon(channel) + " Please provide at least **one** argument.";
		final String error404 = IconChooser.getErrorIcon(channel) + " This user does not exist on https://discord.bio. Try again with another username.";


		if (args.length < 1) {
			channel.sendMessage(missingArgs).queue();
			return;
		}

		try {
			String slug = URLEncoder.encode(String.join(" ", args), "UTF-8");

			final JSONObject object = GeneralUtils.getJSONFromURL(BASE_URL + "userdetails/" + slug);

			if (object == null || !object.getBoolean("success")) {
				channel.sendMessage(error404).queue();
                                return;
                        }
			else {
				final JSONObject settings = object.getJSONObject("payload").getJSONObject("settings");
				final JSONObject discord = object.getJSONObject("payload").getJSONObject("discord");
				final String avatarKey = discord.getString("avatar");
				final String notSet = "Not set";

				final EmbedBuilder eb = new EmbedBuilder().setColor(0xffffff)
						.setAuthor(String.format("%s#%s", discord.getString("username"), discord.getString("discriminator")))
						.setDescription(settings.getString("status"))
						.addField("About", settings.getString("description"), true)
						.addField("Upvotes", String.valueOf(settings.getInt("upvotes")), true)
						.addField("Location", (settings.isNull("location") ? notSet : settings.getString("location")), true)
						.addField("Birthday", (settings.isNull("birthday")? notSet : settings.getString("birthday")), true)
						.addField("E-Mail", (settings.getString("email").equals("") ? notSet : settings.getString("email")), true)
						.addField("Occupation", (settings.isNull("occupation")? notSet : String.valueOf(settings.get("occupation"))), true)
						.addField("Verified", settings.getInt("verified") == 1 ? "Yes" : "No", true)
						.addField("ID", discord.getString("id"), true)
						.addField("Gender", getGender(settings), true)
						.setThumbnail(DISCORD_CDN_AVATARS_BASE_URL + settings.getString("user_id") + "/" + avatarKey + (avatarKey.startsWith("a_") ? ".gif" : ".png"));

				channel.sendMessage(eb.build()).queue();
			}
		} catch (Exception e) {
			NDLogger.getLogger("Command Handler").log(LogType.ERROR, "Failed to url encode query", e);
			channel.sendMessage(error).queue();
		}
	}

	private String getGender(JSONObject settings) {
		Object gender = settings.get("gender");

		if ("null".equals(gender) || gender == null) {
			gender = "Unspecified";
		} else if ((Integer) gender == 1) {
			gender = "Male";
		} else if ((Integer) gender == 2) {
			gender = "Female";
		} else if ((Integer) gender == 3) {
			gender = "Other";
		}
		return String.valueOf(gender);
	}

	@Override
	public String help() {
		return "The first Discord-based discord.bio UI in Java";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
