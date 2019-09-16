/*
 * Copyright (c) JDiscordBots 2019
 * File: Storage.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import net.dv8tion.jda.api.entities.Guild;
/**
 * Interface for permanent (unit-key-value) storage.<br>
 * Can be compared to a {@link java.util.Map Map}&lt;{@link String},{@link java.util.Map Map}&lt;{@link String},{@link String}&gt;&gt;<br>
 * Implementations of this interface should store multiple units. (accessible by name)<br>
 * Each unit consists of multiple key-value entries.
 */
public interface Storage {
	
	/**
	 * reads a String from a given unit and key<br>
	 * Can be compared to <code>get(unit).get(key)</code> but may create necessary elements
	 * @param unit the unit where the data is stored in.
	 * @param key the key of the value that should be read
	 * @param defaultValue the value that should be used (and eventually saved), if the key does not exist
	 * @return the read String
	 * @see Storage#write(String, String, String)
	 * @see Storage#remove(String, String)
	 */
	String read(String unit,String key,String defaultValue);
	/**
	 * writes a String to a key in a given unit<br>
	 * Can be compared to <code>get(unit).put(key,value)</code> but creates the unit if it is non-existent
	 * @param unit the unit where the data should be stored in. 
	 * @param key the key of the where the value should be stored in the unit
	 * @param value the future value of the key in the unit
	 * @see Storage#read(String, String, String)
	 * @see Storage#remove(String, String)
	 */
	void write(String unit,String key,String value);
	/**
	 * removes a key(and its value) from a unit<br>
	 * Can be compared to <code>get(unit).remove(key)</code>
	 * @param unit the unit where the key (and its value) should be removed
	 * @param key the key of the key-value pair to remove
	 * @see Storage#read(String, String, String)
	 * @see Storage#write(String, String, String)
	 */
	void remove(String unit,String key);
	/**
	 * reads the default value for guilds from a given key
	 * @param key the key
	 * @return the (current) default value
	 * @see Storage#setGuildDefault(String, String)
	 */
	String getGuildDefault(String key);
	/**
	 * reads the default value for guilds from a given key
	 * @param key the key
	 * @param value the (future) default value
	 * @see Storage#getGuildDefault(String)
	 */
	void setGuildDefault(String key,String value);
	/**
	 * gets a value from a key that is specific for a guild
	 * @param guild the specific {@link Guild} for this key-value pair
	 * @param key the key that belongs to the value to get
	 * @return the value associated with the key
	 * @see Storage#setForGuild(Guild, String, String)
	 * @see Storage#setGuildDefault(String, String)
	 * @see Storage#getGuildDefault(String)
	 */
	String getForGuild(Guild guild,String key);
	/**
	 * sets a key-value that is specific for a guild
	 * @param guild the specific {@link Guild}, where this key-value pair belongs to
	 * @param key the key of the value to set
	 * @param value the value to set
	 * @see Storage#getForGuild(Guild, String)
	 * @see Storage#setGuildDefault(String, String)
	 * @see Storage#getGuildDefault(String)
	 */
	void setForGuild(Guild guild,String key,String value);
	/**
	 * reloads all cached data
	 */
	default void reload() {
		//do nothing
	}
	/**
	 * reloads the cached data for a Guild
	 * @param guild the Guild
	 */
	default void reload(Guild guild) {
		//do nothing
	}
}
