package io.github.bynoobiyt.nightdream.util;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;

public class BotData {
	private static String defaultPrefix;
	private static final Map<Guild,String> prefixes = new HashMap<>();
	private static String[] adminIDs= {"358291050957111296","321227144791326730","299556333097844736"};
	
	static {
		setDefaultPrefix(null);
	}
	
	private BotData() {
		//prevent Instantiation
	}
	
	public static String getDefaultPrefix() {
		return defaultPrefix;
	}
	public static void setDefaultPrefix(String prefix) {
		if (prefix == null || prefix.equals("")) {
			prefix = "nd-";
		}
		BotData.defaultPrefix = prefix;
	}
	public static final String getPrefix(Guild g) {
		String prefix = prefixes.get(g);
		if (prefix == null) {
			prefix = defaultPrefix;
		}
		return prefix;
	}
	public static void setPrefix(Guild g,String prefix) {
		if (prefix==null || prefix.equals("")) {
			prefixes.remove(g);
		} else {
			prefixes.put(g, prefix);
		}
		
	}

	public static String[] getAdminIDs() {
		return BotData.adminIDs;
	}

	public static void setAdminIDs(String[] adminIDs) {
		BotData.adminIDs = adminIDs;
	}
	
	
}
