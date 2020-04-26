/*
 * Copyright (c) JDiscordBots 2019
 * File: NightDream.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.core;

import io.github.jdiscordbots.nightdream.commands.BotCommand;
import io.github.jdiscordbots.nightdream.commands.Command;
import io.github.jdiscordbots.nightdream.listeners.BotListener;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.JDAImpl;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NightDream {

	public static final String VERSION = "0.0.4";
	
	private static final Logger LOG=LoggerFactory.getLogger(NightDream.class);
	
	public static ShardManager initialize() {
		final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createLight(BotData.getToken(), GatewayIntent.getIntents(GatewayIntent.DEFAULT))
			.setAutoReconnect(true) //should the Bot reconnect?
			.enableCache(EnumSet.of(CacheFlag.VOICE_STATE))
			.setStatus(OnlineStatus.ONLINE) //the online Status
			/*	possible statuses:
				OnlineStatus.DO_NOT_DISTURB
				OnlineStatus.IDLE
				OnlineStatus.INVISIBLE
				OnlineStatus.ONLINE
				OnlineStatus.OFFLINE
				OnlineStatus.UNKNOWN
			*/
			.setActivity(Activity.playing(BotData.getDefaultPrefix() + "help | " + BotData.getGame())) //the name of the game the Bot is "playing"
			/*
				Activity.playing(String)//playing...
				Activity.listening(String)//listening...
				Activity.streaming(String, String)//streaming...(with url)
				Activity.watching(String)//watching...
			*/
			.setRequestTimeoutRetry(true);
		ShardManager bot=null;
		try {
			// initialize commands and listeners
			Reflections ref = new Reflections("io.github.jdiscordbots.nightdream");
			LOG.info("Loading Commands and Listeners...");
			addCommandsAndListeners(ref, builder);
			LOG.info("Loaded Commands and Listeners");
			if(LOG.isInfoEnabled()) {
				LOG.info("available Commands: {}", CommandHandler.getCommands().keySet().stream().collect(Collectors.joining(", ")));
			}
			bot = builder.build();
			LOG.info("Logging in with {} shard/-s.",bot.getShardsTotal());
			bot.getShards().forEach(jda->{
				try {
					jda.awaitReady();
					((JDAImpl) jda).getGuildSetupController().clearCache();
				} catch (InterruptedException e) {
					LOG.warn("The main thread was interruped while waiting for a shard to connect initially",e);
					Thread.currentThread().interrupt();
				}
			});
			LOG.info("Logged in. {}/{} shard/-s online.",bot.getShardsRunning(),bot.getShardsTotal());
			
		} catch (final LoginException e) {
			LOG.error("The entered token is not valid!");
		} catch (final IllegalArgumentException e) {
			LOG.error("There is no token entered!",e);
		}
		return bot;
	}
	public static void main(String[] args) {
		initialize();
	}
	
	/**
	 * adds Commands and Listeners
	 * @param ref The {@link Reflections} Object
	 * @param builder The Builder of the JDA objects(shards)
	 */
	private static void addCommandsAndListeners(Reflections ref,DefaultShardManagerBuilder builder) {
		addAction(ref, BotCommand.class,(cmdAsAnnotation,annotatedAsObject)->{
    		BotCommand cmdAsBotCommand = (BotCommand)cmdAsAnnotation;
    		Command cmd = (Command)annotatedAsObject;
    		for (String alias : cmdAsBotCommand.value()) {
				CommandHandler.addCommand(alias.toLowerCase(), cmd);
			}
		});
		addAction(ref, BotListener.class,(cmdAsAnnotation,annotatedAsObject)->{
    		ListenerAdapter listener = (ListenerAdapter) annotatedAsObject;
    		builder.addEventListeners(listener);
    	});
	}
	/**
	 * invokes Method Objects of all Classes from that are annotated with a specified {@link Annotation}
	 * @param ref The {@link Reflections} Object that scanned the Classes
	 * @param annotClass The Class Object of the Annotation
	 * @param function the code to be executed with every annotated Class
	 */
	private static void addAction(Reflections ref, Class<? extends Annotation> annotClass, BiConsumer<Annotation, Object> function) {
		for (Class<?> cl : ref.getTypesAnnotatedWith(annotClass,true)) {
            try {
				Object annotatedAsObject = cl.getDeclaredConstructor().newInstance();
				Annotation cmdAsAnnotation = cl.getAnnotation(annotClass);
				function.accept(cmdAsAnnotation, annotatedAsObject);
			} catch (InstantiationException e) {
				addActionWarn(cl,annotClass,"cannot be instantiated");
			} catch (IllegalAccessException e) {
				addActionWarn(cl,annotClass,"the no-args constructor is not visible");
			} catch (NoSuchMethodException e) {
				addActionWarn(cl,annotClass,"there is no no-args constructor");
			} catch (InvocationTargetException e) {
				addActionWarn(cl,annotClass,"there was an unknown Error: " + e.getClass().getName()+": "+e.getCause());
			}
        }
    }
	private static void addActionWarn(Class<?> cl,Class<? extends Annotation> annotClass,String err) {
		LOG.warn("{} is annotated with @{} but {}",cl.getName(),annotClass.getName(),err);
	}
}
