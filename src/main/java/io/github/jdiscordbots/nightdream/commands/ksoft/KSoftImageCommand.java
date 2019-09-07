/*
 * Copyright (c) JDiscordBots 2019
 * File: KSoftImageCommand.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.Command;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import io.github.jdiscordbots.nightdream.util.KSoftUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.entities.TaggedImage;
import net.explodingbush.ksoftapi.enums.ImageTag;

public abstract class KSoftImageCommand implements Command {

	protected abstract String getTitle();
	protected abstract ImageTag getImageTag();
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		event.getChannel().sendTyping().complete();
		TaggedImage img = KSoftUtil.getImage(getImageTag());
		if(img==null) {
			JDAUtils.errmsg(event.getChannel(), "This command is disabled due there is no KSoft API token");
			return;
		}
		EmbedBuilder builder=new EmbedBuilder();
		builder.setColor(0x212121)
		.setImage(img.getUrl())
		.setTitle(getTitle())
		.setFooter("Served by an external API - report with " + BotData.getPrefix(event.getGuild()) + "bugreport [url]");
		event.getChannel().sendMessage(builder.build()).queue();
	}
	
	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}
}
