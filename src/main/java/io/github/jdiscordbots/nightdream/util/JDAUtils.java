/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: JDAUtils.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.Color;
import java.util.stream.Stream;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;

/**
 * Utilities for interacting with Discord
 */
public class JDAUtils {
	
	private JDAUtils() {
		//prevent instantiation
	}
	
	/**
	 * sends an Error Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 */
	public static void errmsg(TextChannel channel, String text) {
		msg(channel, text, Color.RED);
	}
	/**
	 * send a Message
	 * standardColor: {@link Color#GREEN}
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, String text) {
		return msg(channel, text, Color.GREEN);
	}
	
	/**
	 * send a Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @param color the {@link Color} of the Message
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, String text,Color color) {
		return msg(channel, new EmbedBuilder()
					.setColor(color)
					.setDescription(text)
					.build());
	}
	/**
	 * send a Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param message The content of the Message as {@link MessageEmbed}
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, MessageEmbed message) {
		try {
			return channel.sendMessage(message).complete();
		} catch (InsufficientPermissionException e) {
			NDLogger.logWithModule(LogType.DEBUG,"message Sender", "Cannot send Message \""+message.getDescription()+"\" in channel "+channel.getName()+"["+channel.getGuild().getName()+"]",e);
			return null;
		}
	}
	/**
	 * tests if a User is one of the Admins of this Bot<br>
	 * @param user the {@link User} that should be checked
	 * @return <code>true</code> if the Author is an Admin, else <code>false</code>
	 */
	public static boolean isOwner(User user) {
		String authorID=user.getId();
		for (String adminID : BotData.getAdminIDs()) {
			if(adminID.equals(authorID)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * tests if the Author of a Message is one of the Admins of this Bot<br>
	 * if not an error message will be sent.
	 * @param event the {@link GuildMessageReceivedEvent} of the Message
	 * @return <code>true</code> if the Author is an Admin, else <code>false</code>
	 */
	public static boolean checkOwner(GuildMessageReceivedEvent event) {
		return checkOwner(event, true);
	}
	/**
	 * tests if the Author of a Message is one of the Admins of this Bot<br>
	 * if forbidden and doErrMsg is true an error message will be sent.
	 * @param event the {@link GuildMessageReceivedEvent} of the Message
	 * @param doErrMsg should an Error-Message be sent?
	 * @return <code>true</code> if the Author is an Admin, else <code>false</code>
	 */
	public static boolean checkOwner(GuildMessageReceivedEvent event,boolean doErrMsg) {
		boolean owner=isOwner(event.getAuthor());
		if (!owner&&doErrMsg) {
			event.getChannel().sendMessage(IconChooser.getInfoIcon(event.getChannel())+" This is an admin command.").queue();
		}
		return owner;
	}
	/**
	 * This method should be called if a token leak occurs.<br>
	 * This shuts down the bot, logs the token leak using the FATAL level and removes the user from the admin IDs.
	 * @param user the {@link User} that caused/commanded the token leak
	 */
	public static void tokenLeakAlert(User user) {
		user.getJDA().shutdownNow();
		NDLogger.logWithModule(LogType.FATAL, "Eval", user.getAsTag() + "(" + user.getId() + ") tried to get the bot token");
		BotData.setAdminIDs(Stream.of(BotData.getAdminIDs()).filter(id->!user.getId().equals(id)).toArray(String[]::new));
	}
}
