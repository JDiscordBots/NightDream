/*
 * Copyright (c) JDiscordBots 2019
 * File: NDLogger.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.logging;

import java.util.HashMap;
import java.util.Map;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

public class NDLogger {
	
	private static final String PROP_PREFIX="io.github.jdiscordbots.nightdream.logging.";
	private static Map<String, NDLogger> loggers=new HashMap<String, NDLogger>();
	private static ColoredPrinter printer;
	
	private static final int DEFAULT_LEVEL; //TODO actually use it
	
	private String module;
	
	static {
		LogType level = LogType.INFO;
		
		String levelProp = System.getProperty(PROP_PREFIX+"Level",level.name()).toUpperCase();
		try{
			level = LogType.valueOf(levelProp);
		}catch (IllegalArgumentException e) {
			//ignore
		}
		DEFAULT_LEVEL=level.getLevel();
		if(!"false".equalsIgnoreCase(System.getProperty(PROP_PREFIX + "colors"))) {
			boolean timestamp=Boolean.parseBoolean(System.getProperty(PROP_PREFIX+"timestamp","false"));
			printer=new ColoredPrinter.Builder(DEFAULT_LEVEL, timestamp).build();
		}
	}

	/**
	 * @param module name of module
	 */
	private NDLogger(String module) {
		this.module=module;
	}

	/**
	 * @return a logger with no module name (global logger)
	 */
	public static NDLogger getGlobalLogger() {
		return getLogger(null);
	}

	/**
	 * @param module mame of module
	 * @return logger with module
	 */
	public static synchronized NDLogger getLogger(String module) {
		if(!loggers.containsKey(module)) {
			loggers.put(module, new NDLogger(module));
		}
		return loggers.get(module);
	}

	/**
	 * @param message message to log
	 */
	public static void logWithoutModule(String message) {
		getGlobalLogger().log(message);
	}

	/**
	 * @param message message to log
	 * @param throwable {@link Throwable} to log
	 */
	public static void logWithoutModule(String message,Throwable throwable) {
		getGlobalLogger().log(null,message,throwable);
	}

	/**
	 * @param level {@link LogType logtype}
	 * @param message message to log
	 */
	public static void logWithoutModule(LogType level,String message) {
		getGlobalLogger().log(level,message);
	}

	/**
	 * @param level {@link LogType logtype}
	 * @param message message to log
	 * @param throwable {@link Throwable} to log
	 */
	public static void logWithoutModule(LogType level,String message,Throwable throwable) {
		getGlobalLogger().log(level,message,throwable);
	}

	/**
	 * @param module name of module
	 * @param message message to log
	 */
	public static void logWithModule(String module,String message) {
		getLogger(module).log(message);
	}

	/**
	 * @param level {@link LogType logtype}
	 * @param module name of module
	 * @param message message to log
	 */
	public static void logWithModule(LogType level,String module,String message) {
		getLogger(module).log(level,message);
	}

	/**
	 * @param level {@link LogType logtype}
	 * @param module name of module
	 * @param message message to log
	 * @param throwable {@link Throwable} to log
	 */
	public static void logWithModule(LogType level,String module,String message,Throwable throwable) {
		getLogger(module).log(level,message,throwable);
	}

	/**
	 * @param message message to log
	 */
	public void log(String message) {
		log(null,message);
	}

	/**
	 * @param level {@link LogType logtype}
	 * @param message message to log
	 * @param throwable {@link Throwable} to log
	 */
	public void log(LogType level,String message,Throwable throwable) {
		if(printer==null) {
			throwable.printStackTrace();
		}else {
			synchronized (System.out) {
				log(level,message);
				throwable.printStackTrace(System.out);
				printer.clear();
			}
		}
	}

	/**
	 * @param level {@link LogType logtype}
	 * @param message message to log
	 */
	public void log(LogType level,String message) {
		synchronized (System.out) {
			if(level==null) {
				level=LogType.DEFAULT;
			}
			if(printer==null) {
				System.out.print(String.format("%-6s",level.getPrefix())+"| "+message);
				if(module!=null) {
					System.out.print(" | "+module);
				}
			}else {
				printer.print(String.format("%-6s",level.getPrefix()), Attribute.NONE, level.getfColor(), level.getbColor());
				printer.clear();
				printer.print("| ");
				printer.print(message,Attribute.LIGHT,FColor.WHITE,BColor.NONE);
				if(module!=null) {
					printer.clear();
					printer.print(" | ");
					printer.print(module);
				}
				printer.clear();
				
			}
			System.out.println();
		}
	}

	/**
	 * Used for logging tests
	 */
	public static void main(String[] args) {
		logWithModule(LogType.DONE, "TEST", "test message",new Exception());
	}
}
