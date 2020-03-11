/*
 * Copyright (c) JDiscordBots 2019
 * File: Eval.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import bsh.EvalError;
import bsh.Interpreter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@BotCommand("eval")
public class Eval implements Command {

	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	private static final Interpreter shell=new Interpreter();

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	private void exec(GuildMessageReceivedEvent event,String script) {
		try {
			long time=System.nanoTime();
			Object result=shell.eval(script);
			time=System.nanoTime()-time;
			if(result != null&&result.toString().contains(BotData.getToken())) {
				JDAUtils.tokenLeakAlert(event.getAuthor());
			}else {
				onSuccess(result,event,time);
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
        int index=event.getMessage().getContentRaw().indexOf(' ');
        String script=index==-1?"":event.getMessage().getContentRaw().substring(index+1);
        if (script.contains("getToken")) {
        	JDAUtils.tokenLeakAlert(event.getAuthor());
		}else {
			new Thread(()->exec(event,script)).start();
		}
	}
	
	protected void onSuccess(Object result,GuildMessageReceivedEvent event,long time) {
		EmbedBuilder eb=new EmbedBuilder();
		eb.setFooter((result==null?"null":result.getClass().getCanonicalName())+" | "+time+"ns");
		if (result != null) {
			String text="```java\n"+result.toString()+"\n```";
			if(text.length()>=2000) {
				eb.setDescription("As the output was over 2000 characters, it was exported into a text file.");
				event.getChannel().sendMessage(eb.build()).addFile(result.toString().getBytes(StandardCharsets.UTF_8), "result.txt").queue();
				eb=null;
			}else {
				eb.setDescription(text);
			}
		}
		if(eb!=null) {
			event.getChannel().sendMessage(eb.build()).queue();
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
		return "Evaluates Code";
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
