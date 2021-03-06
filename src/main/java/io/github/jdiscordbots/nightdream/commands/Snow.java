/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Snow.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import io.github.jdiscordbots.nightdream.util.IconChooser;

@BotCommand("snow")
public class Snow implements Command {

	private static int increment=0;
	private static final long EPOCH=1_420_070_400_000L;//Discord epoch/1.1.2015 0:00
	
	private static final DateTimeFormatter FORMATTER=DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss:SSS (zzz)").withLocale(Locale.ROOT);
	
	private String getTimeString(Instant time) {
		return time.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
				.format(FORMATTER);
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			Instant time=Instant.now();
			event.getChannel().sendMessage("`"+generate(time.toEpochMilli())+"` made `"+getTimeString(time)+"`").queue();
		}else {
			try {
				EmbedBuilder eb=new EmbedBuilder();
				eb.setTitle(args[0]);
				eb.setColor(Color.white);
				String binStr=fillWithZerosBefore(64,Long.toBinaryString(Long.parseLong(args[0])));
				eb.addField("Binary",binStr , false);
				eb.addField("Date/Time", getTimeString(Instant.ofEpochMilli(Long.valueOf(binStr.substring(0,42),2)+EPOCH)),false);
				eb.addField("Increment", Integer.valueOf(binStr.substring(52,64),2).toString(), false);
				eb.addField("Worker, Process ID", args[0]+" has worker ID "+Integer.valueOf(binStr.substring(42,47),2)+" with process ID "+Integer.valueOf(binStr.substring(47,52),2), false);
				event.getChannel().sendMessage(eb.build()).queue();
			}catch(NumberFormatException e) {
				event.getChannel().sendMessage(IconChooser.getErrorIcon(event.getChannel())+" Please provide a valid discord Snowflake.").queue();
			}
		}
	}
	@Override
	public String help() {
		return "Discord ID deconstructor/generator";
	}
	private static String generate(long millis) {
		long diff=millis-EPOCH;
		String timeBinStr=Long.toBinaryString(diff);
		String timeStampStr=fillWithZerosBefore(42,timeBinStr);
		String workerAndProcessIDs="0000100000";
		String binStr=timeStampStr+workerAndProcessIDs+fillWithZerosBefore(12,Integer.toBinaryString((increment++)%4096));
		return Long.valueOf(binStr, 2).toString();
	}
	private static String fillWithZerosBefore(int len,String original) {
		char[] data=new char[len];
		int strlen=original.length();
		Arrays.fill(data,0,data.length-strlen, '0');
		for(int i=data.length-strlen,j=0;i<data.length;i++,j++) {
			data[i]=original.charAt(j);
		}
		return new String(data);
	}
	
	@Override
	public CommandType getType() {
		return CommandType.FUN;
	}
}
