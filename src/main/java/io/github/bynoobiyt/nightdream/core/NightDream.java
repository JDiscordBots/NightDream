package io.github.bynoobiyt.nightdream.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;
import io.github.bynoobiyt.nightdream.commands.BotCommand;
import io.github.bynoobiyt.nightdream.commands.Command;
import io.github.bynoobiyt.nightdream.listeners.BotListener;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;

public class NightDream {

	private static final String ANNOTATED_WITH=" is annotated with @";
	
	public static void main(String[] args) {
		Properties props = new Properties();
		File file = new File("NightDream.properties");
		if (file.exists()) {
			try(FileReader reader=new FileReader(file)){
				props.load(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No Property File found");
			try(FileWriter writer = new FileWriter(file)){
				props.setProperty("token", "");
				props.setProperty("prefix", BotData.getDefaultPrefix());
				props.setProperty("game","with you");
				props.setProperty("admin", BotData.getAdminID());
				props.store(writer,"Nightdream Properties");
				System.out.println("created Properties file with default Properties - please include the Bot token");
				System.exit(1);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BotData.setDefaultPrefix(props.getProperty("prefix",BotData.getDefaultPrefix()));
		BotData.setAdminID(props.getProperty("admin",BotData.getAdminID()));
		
		final JDABuilder builder = new JDABuilder(AccountType.BOT)
		.setToken(props.getProperty("token"))
		.setAutoReconnect(true) //should the Bot reconnect?
		.setStatus(OnlineStatus.ONLINE)//the online Status
		/*possible statuses:
			OnlineStatus.DO_NOT_DISTURB
			OnlineStatus.IDLE
			OnlineStatus.INVISIBLE
			OnlineStatus.ONLINE
			OnlineStatus.OFFLINE
			OnlineStatus.UNKNOWN
		*/
		.setActivity(Activity.playing(props.getProperty("game","with you"))) //the name of the game the Bot is "playing"
		/*
			Game.playing(String)//playing...
			Game.listening(String)//listening...
			Game.streaming(String, String)//streaming...(with url)
			Game.watching(String)//watching...
		*/
		.setRequestTimeoutRetry(true);
		//initialize listeners
		Reflections ref = new Reflections("io.github.bynoobiyt.nightdream");
		addCommandsAndListeners(ref, builder);
		try {
			JDA jda = builder.build();
			jda.awaitReady();

			((JDAImpl) jda).getGuildSetupController().clearCache();
		} catch (final LoginException e) {
			System.err.println("The entered token is not valid!");
		} catch (final IllegalArgumentException e) {
			System.err.println("There is no token entered!");
		} catch (final InterruptedException e) {
			e.printStackTrace();
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
	private static void addAction(Reflections ref,Class<? extends Annotation> annotClass, BiConsumer<Annotation, Object> function) {
		for (Class<?> cl : ref.getTypesAnnotatedWith(annotClass,true)) {
            try {
				Object annotatedAsObject = cl.getDeclaredConstructor().newInstance();
				Annotation cmdAsAnnotation = cl.getAnnotation(annotClass);
				function.accept(cmdAsAnnotation, annotatedAsObject);
			} catch (InstantiationException e) {
				System.err.println(cl.getName() + ANNOTATED_WITH + annotClass.getName() + " but cannot be instantiated");
			} catch (IllegalAccessException e) {
				System.err.println(cl.getName() + ANNOTATED_WITH + annotClass.getName() + " but the no-args constructor is not visible");
			} catch (Exception e) {
				System.err.println(cl.getName() + ANNOTATED_WITH+annotClass.getName() + " but there was an unknown Error: " + e.getClass().getName()+": "+e.getCause());
			}
        }
    }
}
