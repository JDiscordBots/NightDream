/*
 * Copyright (c) JDiscordBots 2019
 * File: SQLStorage.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import io.github.jdiscordbots.nightdream.logging.*;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.*;

/**
 * Implementation of {@link Storage} but with sql databases
 */
public class SQLStorage implements Storage {

	private Connection connection;
	private static final NDLogger LOG = NDLogger.getLogger("Storage");
	
	private final PreparedStatement selectStmt;
	private final PreparedStatement insertStmt;
	private final PreparedStatement updateStmt;
	private final PreparedStatement deleteStmt;
	private final PreparedStatement createStmt;

	public SQLStorage() throws SQLException {
		connection = DriverManager.getConnection(BotData.getDatabaseUrl());
		LOG.log(LogType.DONE, "Successfully connected to database");

		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			if(connection!=null){
				try{
					connection.close();
				}catch(SQLException e){
					LOG.log(LogType.ERROR,"Cannot close DB Connection",e);
				}
			}
		}));
		selectStmt=connection.prepareStatement("SELECT ? FROM ?;");
		insertStmt=connection.prepareStatement("INSERT INTO ? (key, value) VALUES (?, ?);");
		updateStmt=connection.prepareStatement("UPDATE ? SET value = ? WHERE key = ?;");
		deleteStmt=connection.prepareStatement("DELETE FROM ? WHERE key = ?;");
		createStmt=connection.prepareStatement("CREATE TABLE IF NOT EXISTS ? (a varchar(46) primary key,b varchar(46));");
	}

	@Override
	public String read(String unit, String key, String defaultValue) {
		try{
			selectStmt.setString(1, key);
			selectStmt.setString(2, unit);

			try (ResultSet set = selectStmt.executeQuery()) {
				if (set.next()) {
					return set.getString(1);
				} else if (defaultValue != null){
						insertStmt.setString(1, unit);
						insertStmt.setString(2, key);
						insertStmt.setString(3, defaultValue);
						insertStmt.execute();
						return defaultValue;
				} else {
					return null;
				}
			}
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to read sql database", e);
		}
		return null;
	}

	@Override
	public void write(String unit, String key, String value) {
		if (read(unit, key, null) == null) {
			try{
				createStmt.setString(1, unit);
				createStmt.execute();
				insertStmt.setString(1, unit);
				insertStmt.setString(2, key);
				insertStmt.setString(3, value);
				insertStmt.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		} else {
			try{
				updateStmt.setString(1, unit);
				updateStmt.setString(2, value);
				updateStmt.setString(3, key);
				updateStmt.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		}
	}

	@Override
	public void remove(String unit, String key) {
		if (read(unit, key, null) == null) return;
		try{
			deleteStmt.setString(1, unit);
			deleteStmt.setString(2, key);
			deleteStmt.execute();
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to delete sql data", e);
		}
	}

	@Override
	public String getGuildDefault(String key) {
		return read("guild_default", key, null);
	}

	@Override
	public void setGuildDefault(String key, String value) {
		write("guild_default", key, value);
	}

	@Override
	public String getForGuild(Guild guild, String key) {
		return read("guild_" + guild.getId(), key, null);
	}

	@Override
	public void setForGuild(Guild guild, String key, String value) {
		write("guild_" + guild.getId(), key, value);
	}
}
