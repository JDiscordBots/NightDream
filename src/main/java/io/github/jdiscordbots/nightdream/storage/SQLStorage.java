/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: SQLStorage.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.storage;

import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Storage} but with sql databases
 */
public class SQLStorage implements Storage {

	private Connection connection;
	private static final Logger LOG=LoggerFactory.getLogger(SQLStorage.class);
	
	private Map<String, PreparedStatement> stmtBuffer=new HashMap<>();
	
	private static final String DB_WRITE_FAIL_MSG="Failed to write to sql database";
	private static final String DB_READ_FAIL_MSG="Failed to read from sql database";
	private static final String DEFAULT_KEY_NAME="k";
	private static final String DEFAULT_VALUE_NAME="v";
	
	private static final String SELECT_FORMAT="SELECT %s FROM %s WHERE "+DEFAULT_KEY_NAME+"=?";
	private static final String INSERT_FORMAT="INSERT INTO %s ("+DEFAULT_KEY_NAME+",%s) VALUES (?, ?);";
	private static final String UPDATE_FORMAT="UPDATE %s SET %s = ? WHERE "+DEFAULT_KEY_NAME+" = ?;";
	private static final String DELETE_FORMAT="DELETE FROM %s WHERE %s = ?;";
	private static final String CREATE_FORMAT="CREATE TABLE  %s ("+DEFAULT_KEY_NAME+" varchar(100) primary key,"+DEFAULT_VALUE_NAME+" varchar(100));";
	private static final String CREATE_SUB_FORMAT="CREATE TABLE %s ("+DEFAULT_KEY_NAME+" varchar(100) primary key,%s);";

	private final Statement stmt;
	private void close(AutoCloseable toClose) {
		if(toClose!=null) {
			try{
				toClose.close();
			}catch(Exception e){
				LOG.error("Cannot close Statement",e);
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
	private static URL getURL(String str) {
		try {
			return new File(BotData.DATA_DIR,str).toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	public SQLStorage() throws SQLException {
		URL[] urls=Stream.of(BotData.DATA_DIR.list()).map(SQLStorage::getURL).toArray(URL[]::new);
		URLClassLoader loader=AccessController.doPrivileged((PrivilegedAction<URLClassLoader>)(() -> new URLClassLoader(urls)));//NOSONAR this cast is necessary
		ServiceLoader<Driver> drivers = ServiceLoader.load(java.sql.Driver.class, loader);
		Properties info=new Properties();
		if(BotData.getDatabaseUser()!=null&&!"".equals(BotData.getDatabaseUser())) {
			info.setProperty("user", BotData.getDatabaseUser());
			info.setProperty("password", BotData.getDatabasePassword());
		}
		Iterator<Driver> iter=drivers.iterator();
		while (connection==null&&iter.hasNext()) {
			Driver driver=iter.next();
			try {
				connection=driver.connect(BotData.getDatabaseUrl(), info);
			}catch(SQLException e) {
				LOG.warn("Cannot connect to DB with driver {} and URL {}",driver.getClass().getName(),BotData.getDatabaseUrl(),e);
			}
		}
		if(connection==null) {
			connection=DriverManager.getConnection(BotData.getDatabaseUrl(), info);
		}
		LOG.info("Successfully connected to database");
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
			try {
				stmt.execute(creator);
			}catch(SQLException ignore) {
				//ignore if e.g. already exists, if any other error, it will fail later
			}
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
					String.format(CREATE_SUB_FORMAT, unit,Stream.of(defaultRows).map(s->""+s+" varchar(100) default ''").collect(Collectors.joining(", "))));
		} catch (SQLException e) {
			LOG.warn(DB_READ_FAIL_MSG, e);
		}
		if(ret==null||"".equals(ret)) {
			ret=defaultValue;
			write(unit, subUnit, key, defaultValue, defaultRows);
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
			LOG.warn(DB_READ_FAIL_MSG, e);
		}
		if(ret==null||"".equals(ret)) {
			ret=defaultValue;
			write(unit, key, defaultValue);
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
			write(selector,insertStmt,updateStmt,String.format(CREATE_SUB_FORMAT, unit,Stream.of(defaultRows).map(s->""+s+" varchar(100) default ''").collect(Collectors.joining(", "))));
		}catch (SQLException e) {
			LOG.warn(DB_WRITE_FAIL_MSG, e);
		}
	}
	@Override
	public void write(String unit, String key, String value) {
		try {
			PreparedStatement selector=prepareStatement(SELECT_FORMAT,DEFAULT_VALUE_NAME,unit);
			selector.setString(1, key);
			PreparedStatement insertStmt=prepareStatement(INSERT_FORMAT,unit,key);
			insertStmt.setString(1, key);
			insertStmt.setString(2, value);
			PreparedStatement updateStmt=prepareStatement(UPDATE_FORMAT,unit,DEFAULT_VALUE_NAME);
			updateStmt.setString(1, value);
			updateStmt.setString(2, key);
			write(selector,insertStmt,updateStmt,String.format(CREATE_FORMAT, unit));
		}catch (SQLException e) {
			LOG.warn(DB_WRITE_FAIL_MSG, e);
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
			LOG.warn("Failed to delete sql data", e);
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
			LOG.warn(DB_WRITE_FAIL_MSG, e);
		}
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
