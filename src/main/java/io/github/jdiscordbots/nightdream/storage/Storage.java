/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Storage.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.storage;

import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Guild;
/**
 * Interface for permanent (unit-key-value) storage.<br>
 * Can be compared to a {@link java.util.Map Map}&lt;{@link String},{@link java.util.Map Map}&lt;{@link String},{@link String}&gt;&gt;<br>
 * Implementations of this interface should store multiple units. (accessible by name)<br>
 * Each unit consists of multiple key-value entries.
 */
public interface Storage {
	/**
	 * reads a String from a given unit, subunit and key<br>
	 * Can be compared to <code>get(unit).get(key).get(subUnit)</code> but may create necessary elements
	 * @param unit the unit where the data is stored in.
	 * @param subUnit the sub-unit where the data is stored in.
	 * @param key the key of the value that should be read
	 * @param defaultValue the value that should be used (and eventually saved), if the key does not exist
	 * @param defaultSubUnits the sub-units that would be created if there is no entry of them in the table(may not be implemented)
	 * @return the read String
	 */
	String read(String unit,String subUnit, String key, String defaultValue, String... defaultSubUnits);
	
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
	 * writes a String to a key in a given unit and a sub-unit<br>
	 * Can be compared to <code>get(unit).get(subUnit).put(key,value)</code> but creates the unit/sub-unit if it is non-existent
	 * @param unit the unit where the data should be stored in. 
	 * @param subUnit the sub-unit where the data is stored in.
	 * @param key the key of the where the value should be stored in the unit
	 * @param value the future value of the key in the unit
	 * @param defaultSubUnits the sub-units that would be created if there is no entry of them in the table(may not be implemented)
	 */
	public void write(String unit,String subUnit,String key,String value,String... defaultSubUnits);
	
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
	 * removes a key(and its value) from a unit and a sub-unit<br>
	 * Can be compared to <code>get(unit).get(subUnit).remove(key)</code>
	 * @param unit the unit where the key (and its value) should be removed
	 * @param subUnit the sub-unit where the key (and its value) should be removed
	 * @param key the key of the key-value pair to remove
	 * @see Storage#read(String, String, String)
	 * @see Storage#write(String, String, String)
	 */
	void remove(String unit,String subUnit,String key);
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
	default String getGuildDefault(String key) {
		return read("guild_default", key, BotData.GUILD_DEFAULTS.get(key));
	}
	/**
	 * reads the default value for guilds from a given key
	 * @param key the key
	 * @param value the (future) default value
	 * @see Storage#getGuildDefault(String)
	 */
	default void setGuildDefault(String key,String value) {
		write("guild_default", key, value);
	}
	/**
	 * gets a value from a key that is specific for a guild
	 * @param guild the specific {@link Guild} for this key-value pair
	 * @param key the key that belongs to the value to get
	 * @return the value associated with the key
	 * @see Storage#setForGuild(Guild, String, String)
	 * @see Storage#setGuildDefault(String, String)
	 * @see Storage#getGuildDefault(String)
	 */
	default String getForGuild(Guild guild,String key) {
		return read("guild_" + guild.getId(), key, getGuildDefault(key));
	}
	/**
	 * sets a key-value that is specific for a guild
	 * @param guild the specific {@link Guild}, where this key-value pair belongs to
	 * @param key the key of the value to set
	 * @param value the value to set
	 * @see Storage#getForGuild(Guild, String)
	 * @see Storage#setGuildDefault(String, String)
	 * @see Storage#getGuildDefault(String)
	 */
	default void setForGuild(Guild guild,String key,String value) {
		write("guild_" + guild.getId(), key, value);
	}
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
