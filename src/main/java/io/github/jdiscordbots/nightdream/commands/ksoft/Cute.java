/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Cute.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import io.github.jdiscordbots.nightdream.commands.BotCommand;
import net.explodingbush.ksoftapi.image.ImageTag;

@BotCommand("cute")
public class Cute extends KSoftImageCommand {

	@Override
	public String help() {
		return "Shows you a cute picture... Aww :3";
	}

	@Override
	protected String getTitle() {
		return "Here's a cute dog :3";
	}

	@Override
	protected ImageTag getImageTag() {
		return ImageTag.valueOf("dog");
	}
}
