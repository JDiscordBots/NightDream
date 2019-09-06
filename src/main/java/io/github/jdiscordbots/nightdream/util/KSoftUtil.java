/*
 * Copyright (c) JDiscordBots 2019
 * File: KSoftUtil.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import io.github.jdiscordbots.nightdream.logging.NDLogger;
import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.TaggedImage;
import net.explodingbush.ksoftapi.enums.ImageTag;

public class KSoftUtil {
	
	private static KSoftAPI api;
	
	static {
		String token=BotData.getKSoftToken();
		if("".equals(token)) {
			NDLogger.logWithModule("KSoft", "no ksoft token defined");
			api=null;
		}else {
			api=new KSoftAPI(token);
		}
	}
	private KSoftUtil() {
		//prevent instantiation
	}
	
	public static KSoftAPI getApi() {
		return api;
	}
	public static TaggedImage getImage(ImageTag tag) {
		if(api==null) {
			return null;
		}
		return api.getTaggedImage(tag).allowNsfw(false).execute();
	}

}
