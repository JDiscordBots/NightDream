/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: IconChooser.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */
package io.github.jdiscordbots.nightdream.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

public final class IconChooser {
	
	private IconChooser() {
		//prevent instantiation
	}
	private static String getIcon(String name,TextChannel tc,String defaultIcon) {
		if(tc.getGuild().getSelfMember().hasPermission(tc,Permission.MESSAGE_EXT_EMOJI)){
			return BotData.STORAGE.read("icons", name, defaultIcon);
		}
		return defaultIcon;
	}
	
	public static String getErrorIcon(TextChannel tc) {
		return getIcon("X",tc,":no_entry_sign:");
	}
	public static String getArrowIcon(TextChannel tc) {
		return getIcon("This",tc,"=>");
	}
	public static String getSuccessIcon(TextChannel tc) {
		return getIcon("Success",tc,":white_check_mark:");
	}
	public static String getQuestionIcon(TextChannel tc) {
		return getIcon("Provide",tc,"?");
	}
	public static String getInfoIcon(TextChannel tc) {
		return getIcon("Info",tc,":information_source:");
	}
}
