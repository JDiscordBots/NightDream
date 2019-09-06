/*
 * Copyright (c) JDiscordBots 2019
 * File: Storage.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */
package io.github.jdiscordbots.nightdream.util;

import net.dv8tion.jda.api.entities.Guild;

public interface Storage {
	
	String read(String unit,String key,String defaultValue);
	void write(String unit,String key,String value);
	void remove(String unit,String key);
	String getGuildDefault(String key);
	void setGuildDefault(String key,String value);
	String getForGuild(Guild guild,String key);
	void setForGuild(Guild guild,String key,String value);
	void reload();
	void reload(Guild guild);
}
