/*
 * Copyright (c) JDiscordBots 2019
 * File: Lyrics.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.BotCommand;
import io.github.jdiscordbots.nightdream.commands.Command;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import io.github.jdiscordbots.nightdream.util.KSoftUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.Lyric;

import java.awt.*;

@BotCommand("lyrics")
public class Lyrics implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		KSoftAPI api = KSoftUtil.getApi();
		if(api==null) {
			JDAUtils.errmsg(event.getChannel(), "This command is disabled due there is no KSoft API token");
			return;
		}
		
		if(args.length==0) {
			JDAUtils.errmsg(event.getChannel(), "not enough arguments");
		}
		
		event.getChannel().sendTyping().queue();
		String query=String.join(" ", args);
		
		Lyric lyric = api.getLyrics().search(query).execute().get(0);
		if(lyric==null) {
			JDAUtils.errmsg(event.getChannel(), "not found");
			return;
		}
		EmbedBuilder builder=new EmbedBuilder();
		builder.setColor(Color.white)
		.setFooter("Results from Genius")
		.setTitle("Result");
		if(lyric.getAlbums().length==0) {
			builder.addField(lyric.getArtistName(), "in no albums", false);
		}else {
			builder.addField(lyric.getArtistName(),lyric.getAlbums()[0],false)
			.addField(lyric.getFullTitle(), "released "+lyric.getAlbumReleaseYears()[0], false);
		}
		event.getChannel().sendMessage(builder.build()).queue();
	}

	@Override
	public String help() {
		return "Seaches a song by its lyrics";
	}

}
