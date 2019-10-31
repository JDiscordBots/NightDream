/*
 * Copyright (c) JDiscordBots 2019
 * File: Eval.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import bsh.EvalError;
import bsh.Interpreter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@BotCommand("eval")
public class Eval implements Command {

	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	private static final Interpreter shell=new Interpreter();

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
        
        try {
        	shell.set("event", event);
			shell.set("jda", event.getJDA());
			shell.set("guild", event.getGuild());
	        shell.set("channel", event.getChannel());
	        shell.set("message", event.getMessage());
		} catch (EvalError e) {
			NDLogger.logWithoutModule(LogType.WARN, "eval exception while setting command values",e);
		}
        StringBuilder scriptBuilder = new StringBuilder();
        for (String string : args) {
			scriptBuilder.append(string).append(" ");
		}
        String script=scriptBuilder.toString();
        if (script.contains("getToken")) {
        	NDLogger.logWithModule(LogType.FATAL, "Eval", event.getAuthor().getAsTag() + "(" + event.getAuthor().getId() + ") tried to get the bot token");
        	event.getJDA().shutdown();
		}
		try {
			Object result=shell.eval(script);
			if(result.toString().contains(event.getJDA().getToken())) {
				NDLogger.logWithModule(LogType.FATAL, "Eval", event.getAuthor().getAsTag() + "(" + event.getAuthor().getId() + ") tried to get the bot token");
	        	event.getJDA().shutdownNow();
			}else {
				onSuccess(result,event);
			}
		} catch (EvalError|RuntimeException e) {
			try {
				shell.set(LATEST_EXCEPTION_KEY_NAME, e);
			} catch (EvalError e1) {
				NDLogger.logWithoutModule(LogType.WARN, "eval exception while setting error value",e);
			}
			onError(e,event);
		}
	}
	
	protected void onSuccess(Object result,GuildMessageReceivedEvent event) {
		if (result != null) {
        	event.getChannel().sendMessage("```js\n"+result.toString()+"\n```").queue();
		}
	}
	protected void onError(Exception e,GuildMessageReceivedEvent event) {
		e.setStackTrace(new StackTraceElement[0]);
		try(StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw)){
			e.printStackTrace(pw);
			String exStr = sw.getBuffer().toString();
			int len = exStr.length();
			if(len > 1000) {
				len = 1000;
			}
			event.getChannel().sendMessage("`ERROR`\n```java\n" + exStr.substring(0, len) + "\n```").queue();
		} catch (IOException ignored) {
			NDLogger.logWithoutModule(LogType.ERROR, "Error within incorrect user input/eval execution error handling", e);
		}
	}
	@Override
	public String help() {
		return "Evaluates JS Code within Java (why JS????????)";
	}

	@Override
    public String permNeeded() {
    	return "Bot-Admin";
    }
	
	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
