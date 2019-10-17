package io.github.jdiscordbots.nightdream;

import io.github.jdiscordbots.nightdream.core.NightDream;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.JDA;

public class JDALoader {
	public static JDA load() {
		System.setProperty("profile", "test");
		String env=System.getenv("ND_token");
		if(env!=null) {
			BotData.setToken(env);
		}
		if((env=System.getenv("ND_admin"))!=null) {
			BotData.setAdminIDs(env.split(" "));
		}
		BotData.setGame("Unit-Testing");
		return NightDream.initialize();
	}

}
