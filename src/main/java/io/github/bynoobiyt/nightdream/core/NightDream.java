/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: NightDream.java
 * Project: NightDream
 * All rights reserved!
 */

package io.github.bynoobiyt.nightdream.core;

import io.github.bynoobiyt.nightdream.commands.BotCommand;
import io.github.bynoobiyt.nightdream.commands.Command;
import io.github.bynoobiyt.nightdream.listeners.BotListener;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NightDream {

	private static final String ANNOTATED_WITH=" is annotated with @";
	public static final String VERSION = "0.0.4";
	
	private static final Logger LOG=LoggerFactory.getLogger(NightDream.class);
	
	public static void main(String[] args) {
		final JDABuilder builder = new JDABuilder(AccountType.BOT)
			.setToken(BotData.getGlobalProperty("token"))
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
			.setActivity(Activity.playing(BotData.getDefaultPrefix() + "help | " + BotData.getGlobalProperty("game"))) //the name of the game the Bot is "playing"
			/*
				Game.playing(String)//playing...
				Game.listening(String)//listening...
				Game.streaming(String, String)//streaming...(with url)
				Game.watching(String)//watching...
			*/
			.setRequestTimeoutRetry(true);
		//initialize listeners
		Reflections ref = new Reflections("io.github.bynoobiyt.nightdream");
		LOG.info("Loading Commands and Listeners...");
		addCommandsAndListeners(ref, builder);
		LOG.info("Loaded Commands and Listeners");
		if(LOG.isDebugEnabled()) {
			String cmdStr=CommandHandler.getCommands().keySet().stream().collect(Collectors.joining(", "));
			LOG.debug("available Commands: {}",cmdStr);
		}
		
		try {
			LOG.info("Logging in...");
			JDA jda = builder.build();
			jda.awaitReady();
			LOG.info("Logged in.");
			((JDAImpl) jda).getGuildSetupController().clearCache();
		} catch (final LoginException e) {
			LOG.error("The entered token is not valid!");
		} catch (final IllegalArgumentException e) {
			LOG.error("There is no token entered!");
		} catch (final InterruptedException e) {
			LOG.error("The main thread got interrupted while logging in",e);
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * adds Commands and Listeners
	 * @param ref The {@link Reflections} Object
	 * @param jdaBuilder The Builder of the JDA
	 */
	private static void addCommandsAndListeners(Reflections ref,JDABuilder jdaBuilder) {
		addAction(ref, BotCommand.class,(cmdAsAnnotation,annotatedAsObject)->{
    		BotCommand cmdAsBotCommand = (BotCommand)cmdAsAnnotation;
    		Command cmd = (Command)annotatedAsObject;
    		for (String alias : cmdAsBotCommand.value()) {
				CommandHandler.addCommand(alias.toLowerCase(), cmd);
			}
		});
		addAction(ref, BotListener.class,(cmdAsAnnotation,annotatedAsObject)->{
    		ListenerAdapter listener = (ListenerAdapter) annotatedAsObject;
			jdaBuilder.addEventListeners(listener);
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
		if(LOG.isWarnEnabled()) {
			String msg=cl.getName() + ANNOTATED_WITH + annotClass.getName() + " but "+err;
			LOG.warn(msg);
		}
	}
}
