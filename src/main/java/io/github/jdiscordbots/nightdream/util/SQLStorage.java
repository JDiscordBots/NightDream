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
	
	private static final String selectFormat="SELECT `value` FROM %s WHERE `key`=?";
	private static final String insertFormat="INSERT INTO %s VALUES (?, ?);";
	private static final String updateFormat="UPDATE %s SET value = ? WHERE `key` = ?;";
	private static final String deleteFormat="DELETE FROM %s WHERE `key` = ?;";
	private static final String createFormat="CREATE TABLE IF NOT EXISTS %s (`key` varchar(46) primary key,`value` varchar(46));";

	private final Statement stmt;
	
	public SQLStorage() throws SQLException {
		if(BotData.getDatabaseUser()==null||"".equals(BotData.getDatabaseUser())) {
			connection = DriverManager.getConnection(BotData.getDatabaseUrl());
		}else {
			connection = DriverManager.getConnection(BotData.getDatabaseUrl(),BotData.getDatabaseUser(),BotData.getDatabasePassword());
		}
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
		stmt=connection.createStatement();
	}
	private PreparedStatement prepareStatement(String sqlWithoutTable,String tableName) throws SQLException {
		return connection.prepareStatement(String.format(sqlWithoutTable, tableName));//TODO buffering
	}
	@Override
	public String read(String unit, String key, String defaultValue) {
		try {
			stmt.execute(String.format(createFormat,unit));
		}catch(SQLException e) {
			LOG.log(LogType.WARN, "Failed create table if it does not exist", e);
		}
		try(PreparedStatement selectStmt=prepareStatement(selectFormat,unit)){
			selectStmt.setString(1, key);
			try (ResultSet set = selectStmt.executeQuery()) {
				if (set.next()) {
					return set.getString(1);
				} else if (defaultValue != null){
					try(PreparedStatement insertStmt=prepareStatement(insertFormat,unit)){
						insertStmt.setString(1, key);
						insertStmt.setString(2, defaultValue);
						insertStmt.execute();
						return defaultValue;
					}
				} else {
					return defaultValue;
				}
			}
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to read sql database", e);
		}
		return defaultValue;
	}
	@Override
	public void write(String unit, String key, String value) {
		if (read(unit, key, null) == null) {
			try(PreparedStatement insertStmt=prepareStatement(insertFormat,unit)){
				stmt.execute(String.format(createFormat,unit));
				insertStmt.setString(1, key);
				insertStmt.setString(2, value);
				insertStmt.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		} else {
			try(PreparedStatement updateStmt=prepareStatement(updateFormat,unit)){
				updateStmt.setString(1, value);
				updateStmt.setString(2, key);
				updateStmt.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		}
	}

	@Override
	public void remove(String unit, String key) {
		if (read(unit, key, null) == null) return;
		try(PreparedStatement deleteStmt=prepareStatement(deleteFormat,unit)){
			deleteStmt.setString(1, key);
			deleteStmt.execute();
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to delete sql data", e);
		}
	}

	@Override
	public String getGuildDefault(String key) {
		return read("guild_default", key, BotData.GUILD_DEFAULTS.get(key));
	}

	@Override
	public void setGuildDefault(String key, String value) {
		write("guild_default", key, value);
	}

	@Override
	public String getForGuild(Guild guild, String key) {
		return read("guild_" + guild.getId(), key, getGuildDefault(key));
	}

	@Override
	public void setForGuild(Guild guild, String key, String value) {
		write("guild_" + guild.getId(), key, value);
	}
}
