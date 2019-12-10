/*
 * Copyright (c) JDiscordBots 2019
 * File: Hug.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.BotCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.image.ImageTag;

@BotCommand("hug")
public class Hug extends KSoftImageCommand {

	private String executor=null;
	private String target=null;
	
	@Override
	public String help() {
		return "Hugs someone or yourself :)";
	}

	@Override
	public synchronized void action(String[] args, GuildMessageReceivedEvent event) {
		executor=event.getMember().getEffectiveName();
		if(event.getMessage().getMentionedMembers().isEmpty()) {
			target=executor;
		}else{
			target=event.getMessage().getMentionedMembers().get(0).getEffectiveName();
		}	
		super.action(args, event);
	}
	
	@Override
	protected synchronized String getTitle() {
		return "**"+target+"** has been hugged by **"+executor+"**!";
	}

	@Override
	protected ImageTag getImageTag() {
		return ImageTag.valueOf("hug");
	}

}
