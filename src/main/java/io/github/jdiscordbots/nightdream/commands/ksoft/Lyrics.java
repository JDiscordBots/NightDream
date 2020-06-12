/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Lyrics.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.BotCommand;
import io.github.jdiscordbots.nightdream.commands.Command;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import io.github.jdiscordbots.nightdream.util.KSoftUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.lyrics.Album;
import net.explodingbush.ksoftapi.entities.lyrics.Track;

import java.awt.Color;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BotCommand("lyrics")
public class Lyrics implements Command {
	private static final Logger LOG=LoggerFactory.getLogger(Lyrics.class);
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		KSoftAPI api = KSoftUtil.getApi();
		if(api==null) {
			JDAUtils.errmsg(event.getChannel(), "This command is disabled due there is no KSoft API token");
			return;
		}
		
		if(args.length==0) {
			JDAUtils.errmsg(event.getChannel(), "not enough arguments");
			return;
		}
		
		event.getChannel().sendTyping().queue();
		String query=String.join(" ", args);
		api.getLyrics().search(query).executeAsync(tracks->{
			Track track=tracks.get(0);
			if(track==null) {
				event.getChannel().sendMessage("No track found").queue();
			}else {
				OptionalInt released=track.getAlbums().stream().mapToInt(Album::getReleaseYear).min();
				EmbedBuilder builder=new EmbedBuilder();
				String lyrics=track.getLyrics();
				builder.setColor(Color.white)
				.setFooter("Results from KSoft.Si API")
				.setTitle("Found something :mag:");
				builder.addField("Artist: "+track.getArtist().getName(),"Album: "+track.getAlbums().stream().map(Album::getName).collect(Collectors.joining(" / ")),false);
				builder.addField("Song: "+track.getName(), released.isPresent()?"released "+released.getAsInt():"", false);
				builder.addField("Lyrics", lyrics.length()>=300?lyrics.substring(0,300)+"\n...":lyrics, false);
				event.getChannel().sendMessage(builder.build()).queue();
			}
		},err->{
			event.getChannel().sendMessage(IconChooser.getErrorIcon(event.getChannel())+" An error occured trying to get the lyrics");
			LOG.error("An error occured while executing the lyrics command",err);
		});
	}

	@Override
	public String help() {
		return "Seaches a song by its lyrics";
	}

	@Override
	public CommandType getType() {
		return CommandType.FUN;
	}
}
