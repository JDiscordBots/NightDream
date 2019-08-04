package io.github.bynoobiyt.nightdream.util;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;

public class BotData {
	private static String defaultPrefix;
	private static final Map<Guild,String> prefixe=new HashMap<>();
	
	static {
		setDefaultPrefix(null);
	}
	
	private BotData() {
		//prevent Instantiation
	}
	
	public static final String getDefaultPrefix() {
		return defaultPrefix;
	}
	public static void setDefaultPrefix(String prefix) {
		if(prefix==null||prefix.equals("")) {
			prefix="nd-";
		}
		BotData.defaultPrefix = prefix;
	}
	public static final String getPrefix(Guild g) {
		String prefix=prefixe.get(g);
		if(prefix==null) {
			prefix=defaultPrefix;
		}
		return prefix;
	}
	public static void setPrefix(Guild g,String prefix) {
		if(prefix==null||prefix.equals("")) {
			prefixe.remove(g);
		}else {
			prefixe.put(g, prefix);
		}
		
	}
}
