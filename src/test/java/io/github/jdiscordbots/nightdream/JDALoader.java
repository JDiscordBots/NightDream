package io.github.jdiscordbots.nightdream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		BotData.setGame("automated Feature-Testing");
		JDA jda=NightDream.initialize();
		String[] adminIDs = BotData.getAdminIDs();
		List<String> ids=new ArrayList<>(Arrays.asList(adminIDs));
		if(!ids.contains(jda.getSelfUser().getId())) {
			ids.add(jda.getSelfUser().getId());
			BotData.setAdminIDs(ids.stream().toArray(String[]::new));
		}
		return jda;
	}

}
