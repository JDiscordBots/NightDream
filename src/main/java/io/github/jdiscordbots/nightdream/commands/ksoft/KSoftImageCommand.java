/*
 * Copyright (c) JDiscordBots 2019
 * File: KSoftImageCommand.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.Command;
import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import io.github.jdiscordbots.nightdream.util.KSoftUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.image.ImageTag;

public abstract class KSoftImageCommand implements Command {

	protected abstract String getTitle();
	protected abstract ImageTag getImageTag();
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(KSoftUtil.getApi()==null) {
			JDAUtils.errmsg(event.getChannel(), "This command is disabled due there is no KSoft API token");
			return;
		}
		event.getChannel().sendTyping().queue(x->{
			KSoftUtil.getImage(getImageTag(),img->{
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0x212121)
				.setImage(img.getUrl())
				.setTitle(getTitle())
				.setFooter("Served by the KSoft.Si API - report with " + BotData.getPrefix(event.getGuild()) + "bugreport [url]");
				event.getChannel().sendMessage(builder.build()).queue();
			},err->{
				event.getChannel().sendMessage(IconChooser.getErrorIcon(event.getChannel())+" could not load image").queue();
				NDLogger.logWithModule(LogType.ERROR, "image", "could not load image",err);
			});
		});
	}
	
	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}
}
