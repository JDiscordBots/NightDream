/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: BotData.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.storage.PropertyStorage;
import io.github.jdiscordbots.nightdream.storage.RedisStorage;
import io.github.jdiscordbots.nightdream.storage.SQLStorage;
import io.github.jdiscordbots.nightdream.storage.Storage;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * saving, loading, retrieving and setting data of the Bot
 */
public class BotData {
	
	private static final NDLogger STORAGE_LOG = NDLogger.getLogger("Storage");
	
	private static final String PREFIX_PROP_NAME = "prefix";
	
	private static final String INSTANCE_OWNER_PROP_NAME="admin";
	private static final String BUG_ID_PROP_NAME="BugID";
	private static final String BUG_CHAN_PROP_NAME="BugReportChannel";
	private static final String BUG_FIXED_PROP_NAME="FixedBugsChannel";
	private static final String MSGLOG_CHAN_PROP_NAME="MsgLogChannel";
	private static final String KSOFT_TOKEN_PROP_NAME="KSoftToken";
	private static final String PIXA_KEY_PROP_NAME="PixabayAPIKey";
	private static final String DATABASE_URL_PROP_NAME = "DBUrl";
	private static final String DATABASE_USER_PROP_NAME = "DBUsr";
	private static final String DATABASE_PASSWORD_PROP_NAME = "DBPw";
	private static final String JDA_TOKEN_PROP_NAME="token";
	
	public static final Map<String,String> GLOBAL_DEFAULTS;
	public static final Map<String,String> GUILD_DEFAULTS;
	
	private static final PropertyStorage bkpStorage = new PropertyStorage();
	public static final Storage STORAGE;
	
	public static final File DATA_DIR=new File(System.getProperty("profile", "NightDream"));
	
	static {
		Storage tempStorage= bkpStorage;
		Map<String,String> defaults=new HashMap<>();
		defaults.put(JDA_TOKEN_PROP_NAME, "");
		defaults.put("game","Nightdreaming...");
		defaults.put(INSTANCE_OWNER_PROP_NAME, String.join(" ","358291050957111296", "321227144791326730"));
		//defaults.put(BUG_CHAN_PROP_NAME, "");
		defaults.put(BUG_FIXED_PROP_NAME, "");
		defaults.put(BUG_ID_PROP_NAME, "0");
		defaults.put(KSOFT_TOKEN_PROP_NAME, "");
		defaults.put(PIXA_KEY_PROP_NAME, "");
		defaults.put(DATABASE_URL_PROP_NAME, "");
		defaults.put(DATABASE_USER_PROP_NAME, "");
		defaults.put(DATABASE_PASSWORD_PROP_NAME, "");
		GLOBAL_DEFAULTS=Collections.unmodifiableMap(defaults);
		
		defaults=new HashMap<>();
		defaults.put(PREFIX_PROP_NAME, "nd-");
		defaults.put(MSGLOG_CHAN_PROP_NAME, "");
		GUILD_DEFAULTS=Collections.unmodifiableMap(defaults);
		if(!DATA_DIR.exists()) {
			try {
				Files.createDirectories(DATA_DIR.toPath());
			} catch (IOException e) {
				NDLogger.logWithModule(LogType.ERROR,"io", "cannot create directory "+DATA_DIR.getAbsolutePath(), e);
			}
		}
		String dbUrl=bkpStorage.getGlobalProperty(DATABASE_URL_PROP_NAME);
		if (dbUrl == null || "".equals(dbUrl)) {
			//do nothing
		} else if(dbUrl.startsWith("redis")){
			if(dbUrl.equals("redis")) {
				tempStorage=new RedisStorage();
			}else if(dbUrl.startsWith("redis://")) {
				dbUrl=dbUrl.substring("redis://".length());
				tempStorage=new RedisStorage(dbUrl);
				STORAGE_LOG.log(LogType.DEBUG, "Storage was set to redis database");
			}
		}else {
			try {
				tempStorage = new SQLStorage();
				STORAGE_LOG.log(LogType.DEBUG, "Storage was set to database");
			} catch (SQLException e) {
				tempStorage = bkpStorage;
				STORAGE_LOG.log(LogType.WARN, "DB loading failed",e);
			}
		}
		if(tempStorage==bkpStorage) {
			STORAGE_LOG.log(LogType.DEBUG, "Storage was set to properties (files)");
		}
		STORAGE = tempStorage;
	}
	
	private BotData() {
		//prevent Instantiation
	}
	
	/**
	 * gets the Prefix for all guilds with no specified prefix
	 * @return the prefix
	 */
	public static String getDefaultPrefix() {
		return STORAGE.getGuildDefault(PREFIX_PROP_NAME);
	}
	/**
	 * sets the Prefix for all guilds with no specified prefix
	 * @param prefix the prefix
	 */
	public static void setDefaultPrefix(String prefix) {
		if (prefix == null || "".equals(prefix)) {
			prefix = "nd-";
		}
		STORAGE.setGuildDefault(PREFIX_PROP_NAME, prefix);
	}
	/**
	 * gets the prefix of a specified Guild or the default prefix
	 * @param g the {@link Guild}
	 * @return the prefix
	 */
	public static final String getPrefix(Guild g) {
		return STORAGE.getForGuild(g, PREFIX_PROP_NAME);
	}
	/**
	 * sets the prefix of a specified Guild or the default prefix
	 * @param g the {@link Guild}
	 * @param prefix the prefix
	 */
	public static void setPrefix(Guild g,String prefix) {
		STORAGE.setForGuild(g, PREFIX_PROP_NAME, prefix);
	}
	/**
	 * sets the channel for message (delete) logs
	 * @param channelId the ISnowflake id of the channel
	 * @param guild the {@link Guild} where the prefix should be set
	 */
	public static void setMsgLogChannel(String channelId, Guild guild) {
		STORAGE.setForGuild(guild, MSGLOG_CHAN_PROP_NAME, channelId);
	}
	/**
	 * gets the channel for message (delete) logs
	 * @param guild the {@link Guild} where the prefix should be set
	 * @return the ISnowflake id of the channel
	 */
	public static String getMsgLogChannel(Guild guild) {
		return STORAGE.getForGuild(guild, MSGLOG_CHAN_PROP_NAME);
	}
	/**
	 * resets/unsets the channel for message (delete) logs
	 * @param guild the {@link Guild} where the prefix should be reset
	 */
	public static void resetMsgLogChannel(Guild guild) {
		STORAGE.setForGuild(guild, MSGLOG_CHAN_PROP_NAME, "");
	}
	/**
	 * resets the prefix for a {@link Guild} (sets it to the default prefix for all Guilds)
	 * @param g the {@link Guild} where the prefix should be reset
	 */
	public static void resetPrefix(Guild g) {
		setPrefix(g, getDefaultPrefix());
	}
	/**
	 * gets the instance owners of the bot
	 * @return the instance owners as array of ISnowflake IDs
	 */
	public static String[] getAdminIDs() {
		return bkpStorage.getGlobalProperty(INSTANCE_OWNER_PROP_NAME).split(" ");
	}
	/**
	 * sets the instance owners of the bot
	 * @param adminIDs the instance owners as array of ISnowflake IDs
	 */
	public static void setAdminIDs(String[] adminIDs) {
		bkpStorage.setGlobalProperty(INSTANCE_OWNER_PROP_NAME, String.join(" ",adminIDs));
	}
	/**
	 * gets the activity the bot
	 * @return the activity
	 */
	public static String getGame() {
		return bkpStorage.getGlobalProperty("game");
	}
	/**
	 * sets the Discord Bot game
	 * @param game the game
	 */
	public static void setGame(String game) {
		bkpStorage.setGlobalProperty("game", game);
	}
	/**
	 * gets the Discord Bot token
	 * @return the token
	 */
	public static String getToken() {
		return bkpStorage.getGlobalProperty(JDA_TOKEN_PROP_NAME);
	}
	/**
	 * sets the Discord Bot token
	 * @return the token
	 */
	public static void setToken(String token) {
		bkpStorage.setGlobalProperty(JDA_TOKEN_PROP_NAME,token);
	}
	/**
	 * gets the API token from KSoft
	 * @return the KSoft API token
	 */
	public static String getKSoftToken() {
		return bkpStorage.getGlobalProperty(KSOFT_TOKEN_PROP_NAME);
	}
	/**
	 * gets the API key from Pixabay
	 * @return the Pixabay API key
	 */
	public static String getPixaBayAPIKey() {
		return bkpStorage.getGlobalProperty(PIXA_KEY_PROP_NAME);
	}
	
	/**
	 * sets the Bug Report channel of the Bot
	 * @param channelID the ISnowflake ID of the channel
	 */
	public static void setBugReportChannel(String channelID) {
		bkpStorage.setGlobalProperty(BUG_CHAN_PROP_NAME, channelID);
	}
	/**
	 * gets the Bug Report channel of the Bot
	 * @return the ISnowflake ID of the channel
	 */
	public static String getBugReportChannel() {
		return STORAGE.read("bugs", BUG_ID_PROP_NAME, "0");
		//return bkpStorage.getGlobalProperty(BUG_CHAN_PROP_NAME);
	}
	/**
	 * sets the bug current bug ID of the Bot
	 * @param bugID the Bug id
	 */
	public static void setBugID(int bugID) {
		STORAGE.write("bugs", BUG_ID_PROP_NAME, String.valueOf(bugID));
		//bkpStorage.setGlobalProperty(BUG_ID_PROP_NAME, String.valueOf(bugID));
	}
	/**
	 * gets the bug current bug ID of the Bot
	 * @return the Bug id
	 */
	public static int getBugID() {
		return Integer.parseInt(bkpStorage.getGlobalProperty(BUG_ID_PROP_NAME));
	}
	/**
	 * sets the channel for fixed bugs of the bot
	 * @param channelID the id of the channel
	 */
	public static void setFixedBugsChannel(String channelID) {
		bkpStorage.setGlobalProperty(BUG_FIXED_PROP_NAME, channelID);
	}
	/**
	 * gets the channel for fixed bugs of the bot
	 * @return the id of the channel
	 */
	public static String getFixedBugsChannel() {
		return bkpStorage.getGlobalProperty(BUG_FIXED_PROP_NAME);
	}
	/**
	 * gets the database url for database storage
	 * @return database url
	 */
	public static String getDatabaseUrl() {
		return bkpStorage.getGlobalProperty(DATABASE_URL_PROP_NAME);
	}
	/**
	 * sets the database url for database storage
	 * @param databaseUrl database url
	 */
	public static void setDatabaseUrl(String databaseUrl) {
		bkpStorage.setGlobalProperty(DATABASE_URL_PROP_NAME, databaseUrl);
	}
	/**
	 * gets the database username for database storage
	 * @return database username
	 */
	public static String getDatabaseUser() {
		return bkpStorage.getGlobalProperty(DATABASE_USER_PROP_NAME);
	}
	/**
	 * sets the database username for database storage
	 * @param databaseUser database username
	 */
	public static void setDatabaseUser(String databaseUser) {
		bkpStorage.setGlobalProperty(DATABASE_URL_PROP_NAME, databaseUser);
	}
	/**
	 * gets the database password for database storage
	 * @return database password
	 */
	public static String getDatabasePassword() {
		return bkpStorage.getGlobalProperty(DATABASE_PASSWORD_PROP_NAME);
	}
	/**
	 * sets the database password for database storage
	 * @param databasePwd database password
	 */
	public static void setDatabasePassword(String databasePwd) {
		bkpStorage.setGlobalProperty(DATABASE_PASSWORD_PROP_NAME, databasePwd);
	}
	/**
	 * reloads all Properties
	 */
	public static void reloadAllProperties() {
		bkpStorage.reload();
		STORAGE.reload();
	}
	/**
	 * removes the Properties for a Guild
	 * @param guild the {@link Guild}
	 */
	public static void reloadGuildProperties(Guild guild) {
		STORAGE.reload(guild);
	}
}
