/*
 * Copyright (c) JDiscordBots 2019
 * File: PropertyStorage.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Guild;

/**
 * implementation of {@link Storage} but with Property files
 */
public class PropertyStorage implements Storage {
	
	private static final NDLogger LOG=NDLogger.getLogger("storage");
	private static final String FILE_SUFFIX=".properties";
	
	private Properties defaultProps;
	private Properties globalProps;
	private final Map<Guild,Properties> guildProps = new HashMap<>();
	
	private Map<String, Properties> units=new HashMap<>();
	
	@Override
	public String read(String unit,String subUnit, String key, String defaultValue, String... defaultSubUnits) {
		return read(unit,key+"."+subUnit,defaultValue);
	}
	
	@Override
	public String read(String unit, String key,String defaultValue) {
		Properties unitProps;
		if(units.containsKey(unit)) {
			unitProps=units.get(unit);
		}else {
			Properties defaults=new Properties();
			defaults.setProperty(key, defaultValue);
			unitProps = loadPropertiesWithoutGenerating(unit+FILE_SUFFIX,defaults);
		}
		return unitProps.getProperty(key,defaultValue);
	}
	
	@Override
	public void write(String unit,String subUnit,String key,String value,String... defaultSubUnits) {
		write(unit,key+"."+subUnit,value);
	}
	@Override
	public void write(String unit, String key, String value) {
		Properties props;
		if(units.containsKey(unit)) {
			props=units.get(unit);
		}else {
			props = loadPropertiesWithoutGenerating(unit+FILE_SUFFIX,new Properties());
		}
		props.setProperty(key, value);
		saveProperties(unit+FILE_SUFFIX, props, "");
	}
	
	@Override
	public void remove(String unit, String subUnit, String key) {
		remove(unit,key+"."+subUnit);
	}
	
	@Override
	public void remove(String unit, String key) {
		Properties props;
		if(units.containsKey(unit)) {
			props=units.get(unit);
		}else {
			props = loadPropertiesWithoutGenerating(unit+FILE_SUFFIX,new Properties());
		}
		if(props.remove(key)!=null) {
			saveProperties(unit+FILE_SUFFIX, props, "");
		}
	}
	@Override
	public String getGuildDefault(String key) {
		return getDefaultProperties().getProperty(key);
	}
	@Override
	public void setGuildDefault(String key, String value) {
		Properties props=getDefaultProperties();
		props.setProperty(key, value);
		saveGuildDefaultProperties(props);
	}
	@Override
	public String getForGuild(Guild guild, String key) {
		return getGuildSpecificProperties(guild).getProperty(key);
	}
	@Override
	public void setForGuild(Guild guild, String key, String value) {
		Properties props = getGuildSpecificProperties(guild);
		props.setProperty(key, value);
		saveGuildSpecificProperties(props, guild);
	}
	@Override
	public void reload() {
		globalProps=null;
		getGlobalProperties();
		defaultProps=loadGuildDefaultProperties();
		guildProps.clear();
		units.clear();
	}
	@Override
	public void reload(Guild guild) {
		guildProps.remove(guild);
	}
	/**
	 * gets all the Properties of a Guild as a {@link Properties} object
	 * @param g the {@link Guild}
	 * @return the Properties
	 */
	private Properties getGuildSpecificProperties(Guild g) {
		if (!guildProps.containsKey(g)) {
			guildProps.put(g,loadGuildSpecificProperties(g));
		}
		return guildProps.get(g);
	}
	/**
	 * loads all the Properties of a Guild
	 * @param g the {@link Guild}
	 * @return the Properties
	 */
	private Properties loadGuildSpecificProperties(Guild g) {
		return loadPropertiesWithoutGenerating("Guild"+g.getId()+FILE_SUFFIX, getDefaultProperties());
	}
	/**
	 * saves all the Properties of a Guild
	 * @param props the Properties
	 * @param g the {@link Guild}
	 */
	private void saveGuildSpecificProperties(Properties props,Guild g) {
		saveProperties("Guild"+g.getId()+FILE_SUFFIX, props, "Guild specific Properties for Guild "+g.getName());
	}
	/**
	 * saves all the Properties of all {@link Guild}s<br>
	 * These Properties can be overwritten for each {@link Guild}
	 * @param props the Properties
	 */
	private void saveGuildDefaultProperties(Properties props) {
		saveProperties("Guild.properties", props, "Default Properties of Nightdream");
	}
	/**
	 * loads all the Properties of all {@link Guild}s as a {@link Properties} object<br>
	 * These Properties can be overwritten for each {@link Guild}
	 * @return the Properties
	 */
	private Properties loadGuildDefaultProperties() {
		return loadProperties("Guild.properties", BotData.GUILD_DEFAULTS, "Default Properties of Nightdream");
	}
	/**
	 * gets all the Properties of all {@link Guild}s as a {@link Properties} object<br>
	 * These Properties can be overwritten for each {@link Guild}
	 * @return the Properties
	 */
	private Properties getDefaultProperties() {
		if (defaultProps==null) {
			defaultProps=loadGuildDefaultProperties();
		}
		return defaultProps;
	}
	/**
	 * gets a Property that is valid for the whole Bot
	 * @param key the key of the Property
	 * @return the value of the Property
	 */
	public String getGlobalProperty(String key) {
		return getGlobalProperties().getProperty(key);
	}
	/**
	 * sets a Property that is valid for the whole Bot
	 * @param key the key of the Property
	 * @param value the value of the Property
	 */
	public void setGlobalProperty(String key,String value) {
		Properties props=getGlobalProperties();
		props.setProperty(key, value);
		saveProperties("NightDream.properties", props, "Nightdream Properties");
	}
	/**
	 * gets all Properties that are valid for the whole Bot
	 * @return the global {@link Properties}
	 */
	private Properties getGlobalProperties() {
		if(globalProps==null) {
			globalProps=loadProperties("NightDream.properties", BotData.GLOBAL_DEFAULTS, "Nightdream Properties");
		}
		return globalProps;
	}
	/**
	 * loads Properties from a file
	 * @param filename the name of the file(or path relative to the directory to the Bot)
	 * @param defaults the default Properties as {@link Map Map&lt;String,String&gt;}
	 * @param comment a comment for the newly generated file if it does not exist
	 * @return the loaded Properties
	 */
	public Properties loadProperties(String filename,Map<String,String> defaults,String comment) {
		Properties props=new Properties();
		props.putAll(defaults);
		File file=new File(BotData.DATA_DIR,filename);
		if(file.exists()) {
			try(Reader reader=new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), StandardCharsets.UTF_8)){
				props.load(reader);
			} catch (IOException e) {
				LOG.log(LogType.WARN,"Cannot load Properties from file: "+file.getAbsolutePath(),e);
			}
		}else {
			try(Writer writer=new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8)){
				props.store(writer,comment);
			} catch (IOException e) {
				LOG.log(LogType.WARN,"Cannot create file or save Properties: "+file.getAbsolutePath(),e);
			}
		}
		return props;
	}
	/**
	 * loads Properties from a file
	 * @param filename the name of the file(or path relative to the directory to the Bot)
	 * @param defaultProperties the default Properties as {@link Properties}
	 * @return the loaded Properties
	 */
	public Properties loadPropertiesWithoutGenerating(String filename, Properties defaultProperties) {
		Properties props=new Properties(defaultProperties);
		File file=new File(BotData.DATA_DIR,filename);
		if(file.exists()) {
			try(Reader reader=new InputStreamReader(new BufferedInputStream(new FileInputStream(file)),StandardCharsets.UTF_8)){
				props.load(reader);
			} catch (IOException e) {
				LOG.log(LogType.WARN,"Cannot load Properties from file: "+file.getAbsolutePath(),e);
			}
		}
		return props;
	}
	/**
	 * saves Properties to a file
	 * @param filename the name of the file(or path relative to the directory to the Bot)
	 * @param props the data to be saved
	 * @param comment a comment for the newly generated file
	 */
	public void saveProperties(String filename,Properties props, String comment) {
		try(Writer writer=new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(new File(BotData.DATA_DIR,filename))), StandardCharsets.UTF_8.toString())){
			props.store(writer,comment);
		} catch (IOException e) {
			// ignore
		}
	}
	
}
