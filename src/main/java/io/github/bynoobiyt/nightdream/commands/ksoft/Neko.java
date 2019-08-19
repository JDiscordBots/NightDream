package io.github.bynoobiyt.nightdream.commands.ksoft;

import io.github.bynoobiyt.nightdream.commands.BotCommand;

import net.explodingbush.ksoftapi.enums.ImageTag;
@BotCommand("neko")
public class Neko extends KSoftImageCommand{

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
		return ImageTag.NEKO;
	}

}
