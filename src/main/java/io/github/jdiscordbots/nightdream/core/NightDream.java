/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: NightDream.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NightDream {

	private static final Logger LOG;

	public static final String VERSION;

	static {
		LOG = LoggerFactory.getLogger(NightDream.class);
		Properties props = new Properties();
		URL rsc = NightDream.class.getClassLoader().getResource("info.properties");
		if (rsc == null) {
			LOG.error("Resource info.properties not present");
		} else {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(rsc.openStream(), StandardCharsets.UTF_8.name()))) {
				props.load(br);
			} catch (IOException e) {
				LOG.error("Cannot load version", e);
			}
		}
		VERSION = props.getProperty("version", "<unknown>");
	}

	public static ShardManager initialize() {
		final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder
				.createLight(BotData.getToken(), GatewayIntent.getIntents(GatewayIntent.DEFAULT)).setAutoReconnect(true) // should
																															// the
																															// Bot
																															// reconnect?
				.enableCache(EnumSet.of(CacheFlag.VOICE_STATE)).setStatus(OnlineStatus.ONLINE) // the online Status
				/*
				 * possible statuses: OnlineStatus.DO_NOT_DISTURB OnlineStatus.IDLE
				 * OnlineStatus.INVISIBLE OnlineStatus.ONLINE OnlineStatus.OFFLINE
				 * OnlineStatus.UNKNOWN
				 */
				.setActivity(Activity.playing(BotData.getDefaultPrefix() + "help | " + BotData.getGame())) // the name
																											// of the
																											// game the
																											// Bot is
																											// "playing"
				/*
				 * Activity.playing(String)//playing... Activity.listening(String)//listening...
				 * Activity.streaming(String, String)//streaming...(with url)
				 * Activity.watching(String)//watching...
				 */
				.setRequestTimeoutRetry(true);
		ShardManager bot = null;
		try {
			// initialize commands and listeners
			LOG.info("Loading Commands and Listeners...");
			addCommandsAndListeners(builder);
			LOG.info("Loaded Commands and Listeners");
			if (LOG.isInfoEnabled()) {
				LOG.info("available Commands: {}",
						CommandHandler.getCommands().keySet().stream().collect(Collectors.joining(", ")));
			}
			bot = builder.build();
			LOG.info("Logging in with {} shard/-s.", bot.getShardsTotal());
			bot.getShards().forEach(jda -> {
				try {
					jda.awaitReady();
					((JDAImpl) jda).getGuildSetupController().clearCache();
				} catch (InterruptedException e) {
					LOG.warn("The main thread was interruped while waiting for a shard to connect initially", e);
					Thread.currentThread().interrupt();
				}
			});
			LOG.info("Logged in. {}/{} shard/-s online.", bot.getShardsRunning(), bot.getShardsTotal());

		} catch (final LoginException e) {
			LOG.error("The entered token is not valid!");
		} catch (final IllegalArgumentException e) {
			LOG.error("There is no token entered!", e);
		}
		return bot;
	}

	public static void main(String[] args) {
		initialize();
	}

	/**
	 * adds Commands and Listeners
	 * 
	 * @param builder The Builder of the JDA objects(shards)
	 */
	private static void addCommandsAndListeners(DefaultShardManagerBuilder builder) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try {
			addAction(loader.getResources("commands.txt"), BotCommand.class, (cmdAsAnnotation, annotatedAsObject) -> {
				BotCommand cmdAsBotCommand = (BotCommand) cmdAsAnnotation;
				Command cmd = (Command) annotatedAsObject;
				for (String alias : cmdAsBotCommand.value()) {
					CommandHandler.addCommand(alias.toLowerCase(), cmd);
				}
			});
			addAction(loader.getResources("listeners.txt"), BotListener.class, (cmdAsAnnotation, annotatedAsObject) -> {
				ListenerAdapter listener = (ListenerAdapter) annotatedAsObject;
				builder.addEventListeners(listener);
			});
		} catch (IOException e) {
			LOG.error("An error occured while reading the class names for commands and/or listeners.", e);
		}

	}

	/**
	 * invokes Method Objects of all Classes from that are annotated with a
	 * specified {@link Annotation}
	 * 
	 * @param resources  an enumeration of resources where the fully qualified class
	 *                   names can be read
	 * @param annotClass The Class Object of the Annotation
	 * @param function   the code to be executed with every annotated Class
	 * @throws IOException if an I/O error occurred while looking for classes
	 */
	private static void addAction(Enumeration<URL> resources, Class<? extends Annotation> annotClass,
			BiConsumer<Annotation, Object> function) throws IOException {
		String line;
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
				while ((line = reader.readLine()) != null) {
					addAction(line,annotClass,function);
				}
			}
		}
	}
	/**
	 * invokes a method object of a class that is annotated by a specified {@link Annotation}
	 * @param line the fully qualified name of the class
	 * @param annotClass the annotation represented as {@link Class}
	 * @param function the function to execute
	 */
	private static void addAction(String line,Class<? extends Annotation> annotClass,BiConsumer<Annotation, Object> function) {
		Class<?> cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader().loadClass(line);
			Object annotatedAsObject = cl.getDeclaredConstructor().newInstance();
			Annotation cmdAsAnnotation = cl.getAnnotation(annotClass);
			function.accept(cmdAsAnnotation, annotatedAsObject);
		} catch (InstantiationException e) {
			addActionWarn(cl, annotClass, "cannot be instantiated");
		} catch (IllegalAccessException e) {
			addActionWarn(cl, annotClass, "the no-args constructor is not visible");
		} catch (NoSuchMethodException e) {
			addActionWarn(cl, annotClass, "there is no no-args constructor");
		} catch (InvocationTargetException e) {
			addActionWarn(cl, annotClass,
					"there was an unknown Error: " + e.getClass().getName() + ": " + e.getCause());
		} catch (ClassNotFoundException e) {
			LOG.warn("Cannot load class {} that is marked with @{}", line, annotClass);
		}
	}
	private static void addActionWarn(Class<?> cl, Class<? extends Annotation> annotClass, String err) {
		LOG.warn("{} is annotated with @{} but {}", cl.getName(), annotClass.getName(), err);
	}
}
