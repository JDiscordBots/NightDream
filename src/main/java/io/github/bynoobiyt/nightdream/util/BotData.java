package io.github.bynoobiyt.nightdream.util;

import java.io.File;
import java.io.FileNotFoundException;
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
	private static String[] adminIDs= {"358291050957111296","321227144791326730","299556333097844736"};
	private static Properties defaultProps;
	private static final Map<Guild,Properties> guildProps = new HashMap<>();
	
	private BotData() {
		//prevent Instantiation
	}
	
	public static String getDefaultPrefix() {
		return getDefaultProperties().getProperty("prefix","nd-");
	}
	public static void setDefaultPrefix(String prefix) {
		if (prefix == null || prefix.equals("")) {
			prefix = "nd-";
		}
		Properties props=getDefaultProperties();
		props.setProperty("prefix", prefix);
		saveGuildDefaultProperties(props);
	}
	public static final String getPrefix(Guild g) {
		return getProperty("prefix", g);
	}
	public static void setPrefix(Guild g,String prefix) {
		setProperty("prefix", prefix, g);
	}
	public static void resetPrefix(Guild g) {
		setPrefix(g, getDefaultPrefix());
	}
	public static String[] getAdminIDs() {
		return BotData.adminIDs;
	}

	public static void setAdminIDs(String[] adminIDs) {
		BotData.adminIDs = adminIDs;
	}
	private static String getProperty(String key,Guild g) {
		return getGuildSpecificProperties(g).getProperty(key);
	}
	private static void setProperty(String key,String value,Guild g) {
		Properties props=getGuildSpecificProperties(g);
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
		Properties props=new Properties(getDefaultProperties());
		try(Reader reader=new FileReader(new File("Guild"+g.getId()+".properties"))){
			props.load(reader);
		} catch (IOException e) {
			// ignore
		}
		return props;
	}
	private static void saveGuildSpecificProperties(Properties props,Guild g) {
		try(Writer writer=new FileWriter(new File("Guild"+g.getId()+".properties"))){
			props.store(writer,"Guild specific Properties for Guild "+g.getName());
		} catch (IOException e) {
			// ignore
		}
	}
	private static void saveGuildDefaultProperties(Properties props) {
		try(Writer writer=new FileWriter(new File("Guild.properties"))){
			props.store(writer,"Default Properties of Nightdream");
		} catch (IOException e) {
			// ignore
		}
	}
	private static Properties loadGuildDefaultProperties() {
		File file=new File("Guild.properties");
		Properties props=new Properties();
		if(file.exists()) {
			try(Reader reader=new FileReader(file)){
				props.load(reader);
				//setDefaultPrefix(props.getProperty(defaultPrefix,getDefaultPrefix()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try(Writer writer=new FileWriter(file)){
				props.setProperty("prefix", getDefaultPrefix());
				props.store(writer,"Default Properties of Nightdream");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}
	private static Properties getDefaultProperties() {
		if (defaultProps==null) {
			defaultProps=loadGuildDefaultProperties();
		}
		return defaultProps;
	}
}
