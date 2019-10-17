package io.github.jdiscordbots.nightdream;

import io.github.jdiscordbots.nightdream.core.NightDream;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.JDA;

public class JDALoader {
	private static void copyEnv(String... keys) {
		for (String key : keys) {
			String value=System.getenv("ND_"+key);
			if(value!=null) {
				System.setProperty(key, value);
			}
		}
	}
	public static JDA load() {
		copyEnv("token","admin");
		BotData.setGame("Unit-Testing");
		System.setProperty("profile", "test");
		return NightDream.initialize();
	}

}
