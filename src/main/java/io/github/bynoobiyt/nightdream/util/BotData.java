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

import net.dv8tion.jda.api.entities.Guild;

public class BotData {
	private static Properties defaultProps;
	private static final Map<Guild,Properties> guildProps = new HashMap<>();
	private static Properties globalProps;
	private static final String PREFIX_PROP_NAME = "prefix";
	public static final File DATA_DIR = new File("NightDream");
	
	static {
		if(!DATA_DIR.exists()) {
			DATA_DIR.mkdirs();
		}
	}
	
	private BotData() {
		//prevent Instantiation
	}
	
	public static String getDefaultPrefix() {
		return getDefaultProperties().getProperty(PREFIX_PROP_NAME,"nd-");
	}
	public static void setDefaultPrefix(String prefix) {
		if (prefix == null || prefix.equals("")) {
			prefix = "nd-";
		}
		Properties props=getDefaultProperties();
		props.setProperty(PREFIX_PROP_NAME, prefix);
		saveGuildDefaultProperties(props);
	}
	public static final String getPrefix(Guild g) {
		return getProperty(PREFIX_PROP_NAME, g);
	}
	public static void setPrefix(Guild g,String prefix) {
		setProperty(PREFIX_PROP_NAME, prefix, g);
	}
	public static void setMsgLogChannel(String channelId, Guild guild) {
		setProperty("MsgLogChannel", channelId, guild);
	}
	public static String getMsgLogChannel(Guild guild) {
		return getProperty("MsgLogChannel", guild);
	}
	public static void resetMsgLogChannel(Guild guild) {
		setProperty("MsgLogChannel", "", guild);
	}
	public static void resetPrefix(Guild g) {
		setPrefix(g, getDefaultPrefix());
	}
	public static String[] getAdminIDs() {
		return getGlobalProperty("admin").split(" ");
	}
	public static void setAdminIDs(String[] adminIDs) {
		setGlobalProperty("admin", String.join(" ",adminIDs));
	}
	private static String getProperty(String key, Guild g) {
		return getGuildSpecificProperties(g).getProperty(key);
	}
	private static void setProperty(String key, String value, Guild g) {
		Properties props = getGuildSpecificProperties(g);
		props.setProperty(key, value);
		saveGuildSpecificProperties(props, g);
	}
	private static Properties getGuildSpecificProperties(Guild g) {
		if (!guildProps.containsKey(g)) {
			guildProps.put(g,loadGuildSpecificProperties(g));
		}
		return guildProps.get(g);
	}
	private static Properties loadGuildSpecificProperties(Guild g) {
		return loadPropertiesWithoutGenerating("Guild"+g.getId()+".properties", getDefaultProperties());
	}
	private static void saveGuildSpecificProperties(Properties props,Guild g) {
		saveProperties("Guild"+g.getId()+".properties", props, "Guild specific Properties for Guild "+g.getName());
	}
	private static void saveGuildDefaultProperties(Properties props) {
		saveProperties("Guild.properties", props, "Default Properties of Nightdream");
	}
	private static Properties loadGuildDefaultProperties() {
		Map<String,String> defaults=new HashMap<>();
		defaults.put(PREFIX_PROP_NAME, "nd-");
		defaults.put("MsgLogChannel", "");
		return loadProperties("Guild.properties", defaults, "Default Properties of Nightdream");
	}
	private static Properties getDefaultProperties() {
		if (defaultProps==null) {
			defaultProps=loadGuildDefaultProperties();
		}
		return defaultProps;
	}
	public static String getGlobalProperty(String key) {
		return getGlobalProperties().getProperty(key);
	}
	public static void setGlobalProperty(String key,String value) {
		Properties props=getGlobalProperties();
		props.setProperty(key, value);
		saveProperties("NightDream.properties", props, "Nightdream Properties");
	}
	private static Properties getGlobalProperties() {
		if(globalProps==null) {
			Map<String,String> defaults=new HashMap<>();
			defaults.put("token", "");
			defaults.put("game","Nightdreaming...");
			defaults.put("admin", String.join(" ","358291050957111296","321227144791326730","299556333097844736"));
			defaults.put("BugReportChannel", "");
			defaults.put("FixedBugsChannel", "");
			defaults.put("BugID", "0");

			globalProps=loadProperties("NightDream.properties", defaults, "Nightdream Properties");
		}
		return globalProps;
	}
	public static Properties loadProperties(String filename,Map<String,String> defaults,String comment) {
		Properties props=new Properties();
		props.putAll(defaults);
		File file=new File(DATA_DIR,filename);
		if(file.exists()) {
			try(Reader reader=new FileReader(file)){
				props.load(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try(Writer writer=new FileWriter(file)){
				props.store(writer,comment);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}
	public static Properties loadPropertiesWithoutGenerating(String filename, Properties defaultProperties) {
		Properties props=new Properties(defaultProperties);
		File file=new File(DATA_DIR,filename);
		if(file.exists()) {
			try(Reader reader=new FileReader(file)){
				props.load(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}
	public static void saveProperties(String filename,Properties props, String comment) {
		try(Writer writer=new FileWriter(new File(DATA_DIR,filename))){
			props.store(writer,comment);
		} catch (IOException e) {
			// ignore
		}
	}
	public static void setBugReportChannel(String channelID) {
		setGlobalProperty("BugReportChannel", channelID);
	}
	public static String getBugReportChannel() {
		return getGlobalProperty("BugReportChannel");
	}
	public static void setBugID(int bugID) {
		setGlobalProperty("BugID", String.valueOf(bugID));
	}
	public static int getBugID() {
		return Integer.parseInt(getGlobalProperty("BugID"));
	}
	public static void setFixedBugsChannel(String channelID) {
		setGlobalProperty("FixedBugsChannel", channelID);
	}
	public static String getFixedBugsChannel() {
		return getGlobalProperty("FixedBugsChannel");
	}
}
