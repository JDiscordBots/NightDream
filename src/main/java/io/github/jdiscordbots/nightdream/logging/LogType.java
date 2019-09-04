/*
 * Copyright (c) JDiscordBots 2019
 * File: LogType.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.logging;

import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

public enum LogType {
	//TODO fill correct levels
	LOG(80,FColor.WHITE),
	WARN(40,FColor.YELLOW),
	QUESTION(50,FColor.MAGENTA,"?????"),
	INFO(60,FColor.BLUE),
	DONE(60,FColor.GREEN),
	ERROR(30,FColor.RED),
	FATAL(20,FColor.RED),//rainbow???
	DEBUG(90,FColor.CYAN),
	ARROW(1,FColor.RED,BColor.WHITE,">>>>>"),
	DEFAULT(1,FColor.WHITE,BColor.CYAN,"====>");
	
	private String prefix;
	private int level;
	private BColor bColor;
	private FColor fColor;
	
	private LogType(int level,FColor fColor) {
		this(level,fColor,BColor.NONE,null);
		prefix=name();
	}
	private LogType(int level,FColor fColor,BColor bColor) {
		this(level,fColor,bColor,null);
		prefix=name();
	}
	private LogType(int level,FColor fColor,String name) {
		this(level,fColor,BColor.NONE,name);
	}
	private LogType(int level,FColor fColor,BColor bColor,String name) {
		this.prefix=name;
		this.level=level;
		this.fColor=fColor;
		this.bColor=bColor;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public int getLevel() {
		return level;
	}
	public BColor getbColor() {
		return bColor;
	}
	public FColor getfColor() {
		return fColor;
	}
	
	
}
