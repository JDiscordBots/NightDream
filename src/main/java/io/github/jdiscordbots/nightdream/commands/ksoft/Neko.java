/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Neko.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.BotCommand;
import net.explodingbush.ksoftapi.image.ImageTag;
@BotCommand("neko")
public class Neko extends KSoftImageCommand {

	@Override
	public String help() {
		return "Sends a neko image";
	}

	@Override
	protected String getTitle() {
		return "Here's a neko~";
	}

	@Override
	protected ImageTag getImageTag() {
		return ImageTag.valueOf("neko");
	}

}
