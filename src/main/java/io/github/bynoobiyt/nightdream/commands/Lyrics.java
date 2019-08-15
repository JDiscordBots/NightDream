package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import io.github.bynoobiyt.nightdream.util.KSoftUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.Lyric;
//@BotCommand("lyrics")//TODO test
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
		
		event.getChannel().sendTyping().complete();
		String query=String.join(" ", args);
		
		Lyric lyric = api.getLyrics().search(query).execute().get(0);
		if(lyric==null) {
			JDAUtils.errmsg(event.getChannel(), "not found");
			return;
		}
		EmbedBuilder builder=new EmbedBuilder();
		builder.setColor(0x212121)
		.setFooter("Results from Genius")
		.setTitle("Result");
		if(lyric.getAlbums().length==0) {
			builder.addField(lyric.getArtistName(), "in no albums", false);
		}else {
			builder.addField(lyric.getArtistName(),lyric.getAlbums()[0],false)
			.addField(lyric.getFullTitle(), "released "+lyric.getAlbumReleaseYears(), false);
		}
		event.getChannel().sendMessage(builder.build()).queue();
	}

	@Override
	public String help() {
		return "Seaches a song by its lyrics";
	}

}
