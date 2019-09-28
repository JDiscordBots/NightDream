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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link Storage} but with sql databases
 */
public class SQLStorage implements Storage {

	private Connection connection;
	private static final NDLogger LOG = NDLogger.getLogger("Storage");
	
	private Map<String, PreparedStatement> stmtBuffer=new HashMap<>();
	
	private static final String DB_WRITE_FAIL_MSG="Failed to write to sql database";
	private static final String DB_READ_FAIL_MSG="Failed to read from sql database";
	private static final String DEFAULT_KEY_NAME="key";
	private static final String DEFAULT_VALUE_NAME="value";
	
	private static final String SELECT_FORMAT="SELECT `%s` FROM %s WHERE `"+DEFAULT_KEY_NAME+"`=?";
	private static final String INSERT_FORMAT="INSERT INTO %s (`"+DEFAULT_KEY_NAME+"`,`%s`) VALUES (?, ?);";
	private static final String UPDATE_FORMAT="UPDATE `%s` SET `%s` = ? WHERE `"+DEFAULT_KEY_NAME+"` = ?;";
	private static final String DELETE_FORMAT="DELETE FROM `%s` WHERE `%s` = ?;";
	private static final String CREATE_FORMAT="CREATE TABLE IF NOT EXISTS `%s` (`"+DEFAULT_KEY_NAME+"` varchar(46) primary key,`"+DEFAULT_VALUE_NAME+"` varchar(46));";
	private static final String CREATE_SUB_FORMAT="CREATE TABLE IF NOT EXISTS `%s` (`"+DEFAULT_KEY_NAME+"` varchar(46) primary key,%s);";

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
	private PreparedStatement prepareStatement(String sqlWithoutTable,Object... tableName) throws SQLException {
		String format=String.format(sqlWithoutTable, tableName);
		if(stmtBuffer.containsKey(format)) {
			return stmtBuffer.get(format);
		}else {
			PreparedStatement prep=connection.prepareStatement(format);
			stmtBuffer.put(format, prep);
			return prep;
		}
	}
	private String read(PreparedStatement selector,PreparedStatement inserter,String creator) throws SQLException {
		if(creator!=null) {
			stmt.execute(creator);
		}
		try (ResultSet set = selector.executeQuery()) {
			if (set.next()) {
				return set.getString(1);
			} else if (inserter != null){
				inserter.execute();
			}
			return null;
		}
	}
	
	@Override
	public String read(String unit,String subUnit, String key, String defaultValue, String... defaultRows) {
		String ret=null;
		try {
			PreparedStatement selectStmt = prepareStatement(SELECT_FORMAT,subUnit,unit);
			selectStmt.setString(1, key);
			PreparedStatement insertStmt;
			if(defaultValue==null) {
				insertStmt=null;
			}else {
				insertStmt = prepareStatement(INSERT_FORMAT,unit,subUnit);
				insertStmt.setString(1, key);
				insertStmt.setString(2, defaultValue);
			}
			ret = read(selectStmt, insertStmt,
					String.format(CREATE_FORMAT, unit,Stream.of(defaultRows).map(s->"`"+s+"` varchar(46) default ''").collect(Collectors.joining(", "))));
		} catch (SQLException e) {
			LOG.log(LogType.WARN, DB_READ_FAIL_MSG, e);
		}
		if(ret==null) {
			ret=defaultValue;
		}
		return ret;
	}
	@Override
	public String read(String unit, String key, String defaultValue) {
		String ret=null;
		try {
			PreparedStatement selectStmt = prepareStatement(SELECT_FORMAT,DEFAULT_VALUE_NAME,unit);
			selectStmt.setString(1, key);
			PreparedStatement insertStmt;
			if(defaultValue==null) {
				insertStmt=null;
			}else {
				insertStmt = prepareStatement(INSERT_FORMAT,unit,DEFAULT_VALUE_NAME);
				insertStmt.setString(1, key);
				insertStmt.setString(2, defaultValue);
			}
			ret = read(selectStmt, insertStmt,
					String.format(CREATE_FORMAT, unit));
		} catch (SQLException e) {
			LOG.log(LogType.WARN, DB_READ_FAIL_MSG, e);
		}
		if(ret==null) {
			ret=defaultValue;
		}
		return ret;
	}
	private void write(PreparedStatement selector,PreparedStatement inserter,PreparedStatement updater,String creator) throws SQLException {
		if (read(selector,null,creator) == null) {
			stmt.execute(creator);
			inserter.execute();
		} else {
			updater.execute();
		}
	}
	@Override
	public void write(String unit,String subUnit,String key,String value,String... defaultRows) {
		try {
			
			PreparedStatement selector=prepareStatement(SELECT_FORMAT,subUnit,unit);
			selector.setString(1, key);
			PreparedStatement insertStmt=prepareStatement(INSERT_FORMAT,unit,subUnit);
			insertStmt.setString(1, key);
			insertStmt.setString(2, value);
			PreparedStatement updateStmt=prepareStatement(UPDATE_FORMAT,unit,subUnit);
			updateStmt.setString(1, value);
			updateStmt.setString(2, key);
			write(selector,insertStmt,updateStmt,String.format(CREATE_SUB_FORMAT, unit,Stream.of(defaultRows).map(s->"`"+s+"` varchar(46) default ''").collect(Collectors.joining(", "))));
		}catch (SQLException e) {
			LOG.log(LogType.WARN, DB_WRITE_FAIL_MSG, e);
		}
	}
	@Override
	public void write(String unit, String key, String value) {
		try {
			PreparedStatement selector=prepareStatement(SELECT_FORMAT,DEFAULT_VALUE_NAME,unit);
			selector.setString(1, key);
			PreparedStatement insertStmt=prepareStatement(INSERT_FORMAT,unit);
			insertStmt.setString(1, key);
			insertStmt.setString(2, value);
			PreparedStatement updateStmt=prepareStatement(UPDATE_FORMAT,unit,DEFAULT_VALUE_NAME);
			updateStmt.setString(1, value);
			updateStmt.setString(2, key);
			write(selector,insertStmt,updateStmt,String.format(CREATE_FORMAT, unit));
		}catch (SQLException e) {
			LOG.log(LogType.WARN, DB_WRITE_FAIL_MSG, e);
		}
	}

	@Override
	public void remove(String unit, String key) {
		if (read(unit, key, null) == null) return;
		try{
			PreparedStatement deleteStmt=prepareStatement(DELETE_FORMAT,unit,DEFAULT_KEY_NAME);
			deleteStmt.setString(1, key);
			deleteStmt.execute();
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to delete sql data", e);
		}
	}

	@Override
	public void remove(String unit, String subUnit, String key) {
		try {
			PreparedStatement updateStmt=prepareStatement(UPDATE_FORMAT,unit,subUnit);
			updateStmt.setString(1, "");
			updateStmt.setString(2, key);
			updateStmt.execute();
		}catch (SQLException e) {
			LOG.log(LogType.WARN, DB_WRITE_FAIL_MSG, e);
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
