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
import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NightDream {

	private static final String ANNOTATED_WITH=" is annotated with @";
	public static final String VERSION = "0.0.4";
	
	private static final NDLogger LOG=NDLogger.getLogger("System");
	private static final NDLogger CMD_CTL_LOG=NDLogger.getLogger("Command Handler");
	private static final NDLogger DISCORD_CTL_LOG=NDLogger.getLogger("Discord");
	
	public static JDA initialize() {
		final JDABuilder builder = new JDABuilder(AccountType.BOT)
			.setToken(BotData.getToken())
			.setAutoReconnect(true) //should the Bot reconnect?
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
				Game.playing(String)//playing...
				Game.listening(String)//listening...
				Game.streaming(String, String)//streaming...(with url)
				Game.watching(String)//watching...
			*/
			.setRequestTimeoutRetry(true);
		JDA jda=null;
		try {
			DISCORD_CTL_LOG.log(LogType.INFO, "Logging in...");
			
			jda = builder.build();

			// initialize commands and listeners
			Reflections ref = new Reflections("io.github.jdiscordbots.nightdream");
			CMD_CTL_LOG.log(LogType.INFO, "Loading Commands and Listeners...");
			addCommandsAndListeners(ref, jda);
			CMD_CTL_LOG.log(LogType.INFO, "Loaded Commands and Listeners");
			CMD_CTL_LOG.log(LogType.DEBUG, "available Commands: "
					+ CommandHandler.getCommands().keySet().stream().collect(Collectors.joining(", ")));
			jda.awaitReady();
			DISCORD_CTL_LOG.log(LogType.INFO, "Logged in.");
			((JDAImpl) jda).getGuildSetupController().clearCache();
		} catch (final LoginException e) {
			DISCORD_CTL_LOG.log(LogType.ERROR, "The entered token is not valid!");
		} catch (final IllegalArgumentException e) {
			DISCORD_CTL_LOG.log(LogType.ERROR, "There is no token entered!");
		} catch (final InterruptedException e) {
			NDLogger.getGlobalLogger().log(LogType.ERROR, "The main thread got interrupted while logging in", e);
			Thread.currentThread().interrupt();
		}
		return jda;
	}
	public static void main(String[] args) {
		initialize();
	}
	
	/**
	 * adds Commands and Listeners
	 * @param ref The {@link Reflections} Object
	 * @param jda The Builder of the JDA
	 */
	private static void addCommandsAndListeners(Reflections ref,JDA jda) {
		addAction(ref, BotCommand.class,(cmdAsAnnotation,annotatedAsObject)->{
    		BotCommand cmdAsBotCommand = (BotCommand)cmdAsAnnotation;
    		Command cmd = (Command)annotatedAsObject;
    		for (String alias : cmdAsBotCommand.value()) {
				CommandHandler.addCommand(alias.toLowerCase(), cmd);
			}
		});
		addAction(ref, BotListener.class,(cmdAsAnnotation,annotatedAsObject)->{
    		ListenerAdapter listener = (ListenerAdapter) annotatedAsObject;
    		jda.addEventListener(listener);
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
				addActionWarn(cl,annotClass,"the there is no no-args constructor");
			} catch (InvocationTargetException e) {
				addActionWarn(cl,annotClass,"there was an unknown Error: " + e.getClass().getName()+": "+e.getCause());
			}
        }
    }
	private static void addActionWarn(Class<?> cl,Class<? extends Annotation> annotClass,String err) {
		LOG.log(LogType.WARN,cl.getName() + ANNOTATED_WITH + annotClass.getName() + " but "+err);
	}
}
