package io.github.jdiscordbots.nightdream;

import io.github.jdiscordbots.nightdream.core.NightDream;
import net.dv8tion.jda.api.JDA;

public class JDALoader {
	public static JDA load() {
		System.setProperty("profile", "test");
		return NightDream.initialize();
	}

}
