/*
 * Copyright (c) JDiscordBots 2020
 * File: Bio.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import bio.discord.dbio.Dbio;
import bio.discord.dbio.entities.User;
import bio.discord.dbio.entities.user.DiscordInformation;
import bio.discord.dbio.entities.user.SettingsInformation;

@BotCommand("bio")
public class Bio implements Command {
	

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		final TextChannel channel = event.getChannel();
		final String error404 = IconChooser.getErrorIcon(channel) + " This user does not exist on https://discord.bio. Try again with another username.";


		if (args.length < 1) {
			args=new String[] {event.getAuthor().getId()};
		}

		final String slug = String.join(" ", args);
		Dbio.getUserDetails(slug).thenAccept(details->{
			if (!details.isPresent()) {
				channel.sendMessage(error404).queue();
			}
			else {
				User user = details.get();
				DiscordInformation discordInformation = user.getDiscordInformation();
				SettingsInformation settingsInformation = user.getSettingsInformation();
				settingsInformation.getLocation();
				final EmbedBuilder eb = new EmbedBuilder().setColor(0xffffff)
						.setAuthor(discordInformation.getFullUserUsername(), "https://discord.bio/p/" + slug, discordInformation.getAvatarUrl("png"))
						.setDescription(settingsInformation.getDescription())
						.addField("Upvotes", String.valueOf(settingsInformation.getUpvotes()), true)
						.addField("Location", removeNull(settingsInformation.getLocation()), true)
						.addField("Birthday", settingsInformation.getBirthday()==null?"Not set":settingsInformation.getBirthday().toString(), true)
						.addField("E-Mail", removeNull(settingsInformation.getEmail()), true)
						.addField("Occupation", removeNull(settingsInformation.getEmail()), true)
						.addField("Verified", settingsInformation.isVerified() ? "Yes" : "No", true)
						.addField("ID", discordInformation.getId(), true)
						.addField("Gender", settingsInformation.getGender().toString(), true)
						.setThumbnail(discordInformation.getAvatarUrl("png"));

				channel.sendMessage(eb.build()).queue();
			}
		});
	}
	private String removeNull(String in) {
		return in==null?"Not set":in;
	}
	@Override
	public String help() {
		return "A Discord-based discord.bio UI in Java";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}
}
