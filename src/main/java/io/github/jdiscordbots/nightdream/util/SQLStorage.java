/*
 * Copyright (c) JDiscordBots 2019
 * File: SQLStorage.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import io.github.jdiscordbots.nightdream.logging.*;
import net.dv8tion.jda.api.entities.Guild;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link Storage} but with sql databases
 */
public class SQLStorage implements Storage {

	private Connection connection;
	private static final NDLogger LOG = NDLogger.getLogger("Storage");
	
	private Map<String, PreparedStatement> stmtBuffer=new HashMap<>();
	
	private static final String SELECT_FORMAT="SELECT `value` FROM %s WHERE `key`=?";
	private static final String INSERT_FORMAT="INSERT INTO %s VALUES (?, ?);";
	private static final String UPDATE_FORMAT="UPDATE %s SET value = ? WHERE `key` = ?;";
	private static final String DELETE_FORMAT="DELETE FROM %s WHERE `key` = ?;";
	private static final String CREATE_FORMAT="CREATE TABLE IF NOT EXISTS %s (`key` varchar(46) primary key,`value` varchar(46));";

	private final Statement stmt;
	private void close(AutoCloseable toClose) {
		if(toClose!=null) {
			try{
				toClose.close();
			}catch(Exception e){
				LOG.log(LogType.ERROR,"Cannot close Statement",e);
			}
		}
	}
	private void closeAll() {
		for (PreparedStatement pStmt : stmtBuffer.values()) {
			close(pStmt);
		}
		close(stmt);
		close(connection);
	}
	public SQLStorage() throws SQLException {
		if(BotData.getDatabaseUser()==null||"".equals(BotData.getDatabaseUser())) {
			connection = DriverManager.getConnection(BotData.getDatabaseUrl());
		}else {
			connection = DriverManager.getConnection(BotData.getDatabaseUrl(),BotData.getDatabaseUser(),BotData.getDatabasePassword());
		}
		LOG.log(LogType.DONE, "Successfully connected to database");
		stmt=connection.createStatement();
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::closeAll));
		
	}
	private PreparedStatement prepareStatement(String sqlWithoutTable,String tableName) throws SQLException {
		String format=String.format(sqlWithoutTable, tableName);
		if(stmtBuffer.containsKey(format)) {
			return stmtBuffer.get(format);
		}else {
			PreparedStatement stmt=connection.prepareStatement(format);
			stmtBuffer.put(format, stmt);
			return stmt;
		}
	}
	@Override
	public String read(String unit, String key, String defaultValue) {
		try {
			stmt.execute(String.format(CREATE_FORMAT,unit));
		}catch(SQLException e) {
			LOG.log(LogType.WARN, "Failed create table if it does not exist", e);
		}
		try{
			PreparedStatement selectStmt=prepareStatement(SELECT_FORMAT,unit);
			selectStmt.setString(1, key);
			try (ResultSet set = selectStmt.executeQuery()) {
				if (set.next()) {
					return set.getString(1);
				} else if (defaultValue != null){
					PreparedStatement insertStmt=prepareStatement(INSERT_FORMAT,unit);
					insertStmt.setString(1, key);
					insertStmt.setString(2, defaultValue);
					insertStmt.execute();
					
				}
				return defaultValue;
			}
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to read sql database", e);
		}
		return defaultValue;
	}
	@Override
	public void write(String unit, String key, String value) {
		if (read(unit, key, null) == null) {
			try{
				PreparedStatement insertStmt=prepareStatement(INSERT_FORMAT,unit);
				stmt.execute(String.format(CREATE_FORMAT,unit));
				insertStmt.setString(1, key);
				insertStmt.setString(2, value);
				insertStmt.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		} else {
			try{
				PreparedStatement updateStmt=prepareStatement(UPDATE_FORMAT,unit);
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
		try{
			PreparedStatement deleteStmt=prepareStatement(DELETE_FORMAT,unit);
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
	@Override
	public void reload() {
		for (PreparedStatement pStmt : stmtBuffer.values()) {
			close(pStmt);
		}
		stmtBuffer.clear();
	}
	@Override
	public void reload(Guild guild) {
		Set<String> toRemove=new HashSet<>();
		for (String sql : stmtBuffer.keySet()) {
			if(sql.contains(guild.getId())) {
				toRemove.add(sql);
			}
		}
		for (String rem : toRemove) {
			close(stmtBuffer.get(rem));
			stmtBuffer.remove(rem);
		}
	}
}
