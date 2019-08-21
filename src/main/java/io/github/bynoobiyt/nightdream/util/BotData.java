package io.github.bynoobiyt.nightdream.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bynoobiyt.nightdream.commands.Profile;
import net.dv8tion.jda.api.entities.Guild;

/**
 * saving, loading, retrieving and setting data of the Bot
 * @author Daniel Schmid
 */
public class BotData {
	private static Properties defaultProps;
	private static final Map<Guild,Properties> guildProps = new HashMap<>();
	private static Properties globalProps;
	private static final String PREFIX_PROP_NAME = "prefix";
	public static final File DATA_DIR = new File("NightDream");
	
	private static final String INSTANCE_OWNER_PROP_NAME="admin";
	private static final String BUG_ID_PROP_NAME="BugID";
	private static final String BUG_CHAN_PROP_NAME="BugReportChannel";
	private static final String BUG_FIXED_PROP_NAME="FixedBugsChannel";
	private static final String MSGLOG_CHAN_PROP_NAME="MsgLogChannel";
	private static final String KSOFT_TOKEN_PROP_NAME="KSoftToken";
	private static final String PIXA_KEY_PROP_NAME="PixabayAPIKey";
	
	private static final Logger LOG=LoggerFactory.getLogger(BotData.class);
	
	static {
		if(!DATA_DIR.exists()) {
			DATA_DIR.mkdirs();
		}
	}
	
	private BotData() {
		//prevent Instantiation
	}
	
	/**
	 * gets the Prefix for all guilds with no specified prefix
	 * @return the prefix
	 */
	public static String getDefaultPrefix() {
		return getDefaultProperties().getProperty(PREFIX_PROP_NAME,"nd-");
	}
	/**
	 * sets the Prefix for all guilds with no specified prefix
	 * @param prefix the prefix
	 */
	public static void setDefaultPrefix(String prefix) {
		if (prefix == null || prefix.equals("")) {
			prefix = "nd-";
		}
		Properties props=getDefaultProperties();
		props.setProperty(PREFIX_PROP_NAME, prefix);
		saveGuildDefaultProperties(props);
	}
	/**
	 * gets the prefix of a specified Guild or the default prefix
	 * @param g the {@link Guild}
	 * @return the prefix
	 */
	public static final String getPrefix(Guild g) {
		return getProperty(PREFIX_PROP_NAME, g);
	}
	/**
	 * sets the prefix of a specified Guild or the default prefix
	 * @param g the {@link Guild}
	 * @param prefix the prefix
	 */
	public static void setPrefix(Guild g,String prefix) {
		setProperty(PREFIX_PROP_NAME, prefix, g);
	}
	/**
	 * sets the channel for message (delete) logs
	 * @param channelId the ISnowflake id of the channel
	 * @param guild the {@link Guild} where the prefix should be set
	 */
	public static void setMsgLogChannel(String channelId, Guild guild) {
		setProperty(MSGLOG_CHAN_PROP_NAME, channelId, guild);
	}
	/**
	 * gets the channel for message (delete) logs
	 * @param guild the {@link Guild} where the prefix should be set
	 * @return the ISnowflake id of the channel
	 */
	public static String getMsgLogChannel(Guild guild) {
		return getProperty(MSGLOG_CHAN_PROP_NAME, guild);
	}
	/**
	 * resets/unsets the channel for message (delete) logs
	 * @param guild the {@link Guild} where the prefix should be reset
	 */
	public static void resetMsgLogChannel(Guild guild) {
		setProperty(MSGLOG_CHAN_PROP_NAME, "", guild);
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
		return getGlobalProperty(INSTANCE_OWNER_PROP_NAME).split(" ");
	}
	/**
	 * sets the instance owners of the bot
	 * @param adminIDs the instance owners as array of ISnowflake IDs
	 */
	public static void setAdminIDs(String[] adminIDs) {
		setGlobalProperty(INSTANCE_OWNER_PROP_NAME, String.join(" ",adminIDs));
	}
	/**
	 * gets the API token from KSoft
	 * @return the KSoft API token
	 */
	public static String getKSoftToken() {
		return getGlobalProperty(KSOFT_TOKEN_PROP_NAME);
	}
	/**
	 * gets the API key from Pixabay
	 * @return the Pixabay API key
	 */
	public static String getPixaBayAPIKey() {
		return getGlobalProperty(PIXA_KEY_PROP_NAME);
	}
	/**
	 * gets a Property for a {@link Guild}
	 * @param key the key(name) of the Property
	 * @param g the {@link Guild}
	 * @return the value of the Property
	 */
	private static String getProperty(String key, Guild g) {
		return getGuildSpecificProperties(g).getProperty(key);
	}
	/**
	 * sets a Property for a {@link Guild}
	 * @param key the key(name) of the Property
	 * @param value the value of the Property
	 * @param g the {@link Guild}
	 */
	private static void setProperty(String key, String value, Guild g) {
		Properties props = getGuildSpecificProperties(g);
		props.setProperty(key, value);
		saveGuildSpecificProperties(props, g);
	}
	/**
	 * gets all the Properties of a Guild as a {@link Properties} object
	 * @param g the {@link Guild}
	 * @return the Properties
	 */
	private static Properties getGuildSpecificProperties(Guild g) {
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
	private static Properties loadGuildSpecificProperties(Guild g) {
		return loadPropertiesWithoutGenerating("Guild"+g.getId()+".properties", getDefaultProperties());
	}
	/**
	 * saves all the Properties of a Guild
	 * @param props the Properties
	 * @param g the {@link Guild}
	 */
	private static void saveGuildSpecificProperties(Properties props,Guild g) {
		saveProperties("Guild"+g.getId()+".properties", props, "Guild specific Properties for Guild "+g.getName());
	}
	/**
	 * saves all the Properties of all {@link Guild}s<br>
	 * These Properties can be overwritten for each {@link Guild}
	 * @param props the Properties
	 */
	private static void saveGuildDefaultProperties(Properties props) {
		saveProperties("Guild.properties", props, "Default Properties of Nightdream");
	}
	/**
	 * loads all the Properties of all {@link Guild}s as a {@link Properties} object<br>
	 * These Properties can be overwritten for each {@link Guild}
	 * @return the Properties
	 */
	private static Properties loadGuildDefaultProperties() {
		Map<String,String> defaults=new HashMap<>();
		defaults.put(PREFIX_PROP_NAME, "nd-");
		defaults.put(MSGLOG_CHAN_PROP_NAME, "");
		return loadProperties("Guild.properties", defaults, "Default Properties of Nightdream");
	}
	/**
	 * gets all the Properties of all {@link Guild}s as a {@link Properties} object<br>
	 * These Properties can be overwritten for each {@link Guild}
	 * @return the Properties
	 */
	private static Properties getDefaultProperties() {
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
	public static String getGlobalProperty(String key) {
		return getGlobalProperties().getProperty(key);
	}
	/**
	 * sets a Property that is valid for the whole Bot
	 * @param key the key of the Property
	 * @param value the value of the Property
	 */
	public static void setGlobalProperty(String key,String value) {
		Properties props=getGlobalProperties();
		props.setProperty(key, value);
		saveProperties("NightDream.properties", props, "Nightdream Properties");
	}
	/**
	 * gets all Properties that are valid for the whole Bot
	 * @return the global {@link Properties}
	 */
	private static Properties getGlobalProperties() {
		if(globalProps==null) {
			Map<String,String> defaults=new HashMap<>();
			defaults.put("token", "");
			defaults.put("game","Nightdreaming...");
			defaults.put(INSTANCE_OWNER_PROP_NAME, String.join(" ","358291050957111296","321227144791326730","299556333097844736"));
			defaults.put(BUG_CHAN_PROP_NAME, "");
			defaults.put(BUG_FIXED_PROP_NAME, "");
			defaults.put(BUG_ID_PROP_NAME, "0");
			defaults.put(KSOFT_TOKEN_PROP_NAME, "");
			defaults.put(PIXA_KEY_PROP_NAME, "");
			
			globalProps=loadProperties("NightDream.properties", defaults, "Nightdream Properties");
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
	public static Properties loadProperties(String filename,Map<String,String> defaults,String comment) {
		Properties props=new Properties();
		props.putAll(defaults);
		File file=new File(DATA_DIR,filename);
		if(file.exists()) {
			try(Reader reader=new FileReader(file)){
				props.load(reader);
			} catch (IOException e) {
				LOG.warn("Cannot load Properties from file: "+file.getAbsolutePath(),e);
			}
		}else {
			try(Writer writer=new FileWriter(file)){
				props.store(writer,comment);
			} catch (IOException e) {
				LOG.warn("Cannot create file or save Properties: "+file.getAbsolutePath(),e);
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
	public static Properties loadPropertiesWithoutGenerating(String filename, Properties defaultProperties) {
		Properties props=new Properties(defaultProperties);
		File file=new File(DATA_DIR,filename);
		if(file.exists()) {
			try(Reader reader=new FileReader(file)){
				props.load(reader);
			} catch (IOException e) {
				LOG.warn("Cannot load Properties from file: "+file.getAbsolutePath(),e);
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
	public static void saveProperties(String filename,Properties props, String comment) {
		try(Writer writer=new FileWriter(new File(DATA_DIR,filename))){
			props.store(writer,comment);
		} catch (IOException e) {
			// ignore
		}
	}
	/**
	 * sets the Bug Report channel of the Bot
	 * @param channelID the ISnowflake ID of the channel
	 */
	public static void setBugReportChannel(String channelID) {
		setGlobalProperty(BUG_CHAN_PROP_NAME, channelID);
	}
	/**
	 * gets the Bug Report channel of the Bot
	 * @return the ISnowflake ID of the channel
	 */
	public static String getBugReportChannel() {
		return getGlobalProperty(BUG_CHAN_PROP_NAME);
	}
	/**
	 * sets the bug current bug ID of the Bot
	 * @param bugID the Bug id
	 */
	public static void setBugID(int bugID) {
		setGlobalProperty(BUG_ID_PROP_NAME, String.valueOf(bugID));
	}
	/**
	 * gets the bug current bug ID of the Bot
	 * @return the Bug id
	 */
	public static int getBugID() {
		return Integer.parseInt(getGlobalProperty(BUG_ID_PROP_NAME));
	}
	/**
	 * sets the channel for fixed bugs of the bot
	 * @param channelID the id of the channel
	 */
	public static void setFixedBugsChannel(String channelID) {
		setGlobalProperty(BUG_FIXED_PROP_NAME, channelID);
	}
	/**
	 * gets the channel for fixed bugs of the bot
	 * @return the id of the channel
	 */
	public static String getFixedBugsChannel() {
		return getGlobalProperty(BUG_FIXED_PROP_NAME);
	}
	/**
	 * reloads all Properties
	 */
	public static void reloadAllProperties() {
		globalProps=null;
		getGlobalProperties();
		defaultProps=loadGuildDefaultProperties();
		guildProps.clear();
		Profile.reload();
	}
	/**
	 * removes the Properties for a Guilld
	 * @param guild the {@link Guild}
	 */
	public static void reloadGuildProperties(Guild guild) {
		guildProps.remove(guild);
	}
}
