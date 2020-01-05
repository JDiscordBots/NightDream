/*
 * Copyright (c) JDiscordBots 2019
 * File: KSoftUtil.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import java.util.function.Consumer;

import io.github.jdiscordbots.nightdream.logging.NDLogger;
import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.TaggedImage;
import net.explodingbush.ksoftapi.image.ImageTag;

/**
 * Utilities for interacting with the KSoft API
 */
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
	/**
	 * gets the the main Object for interacting with the API
	 * @return the API Object
	 */
	public static KSoftAPI getApi() {
		return api;
	}
	/**
	 * gets an image with a specified tag
	 * @param tag the tag of the image
	 * @return the image as {@link TaggedImage}
	 */
	public static TaggedImage getImage(ImageTag tag) {
		if(api==null) {
			return null;
		}
		return api.getTaggedImage(tag).allowNsfw(false).execute();
	}
	/**
	 * gets an image with a specified tag
	 * @param tag the tag of the image
	 * @return the image as {@link TaggedImage}
	 */
	public static void getImage(ImageTag tag,Consumer<? super TaggedImage> success,Consumer<? super Throwable> failure) {
		if(api==null) {
			failure.accept(new NullPointerException("token not provided"));
			return;
		}
		api.getTaggedImage(tag).allowNsfw(false).executeAsync(success,failure);
	}
}
