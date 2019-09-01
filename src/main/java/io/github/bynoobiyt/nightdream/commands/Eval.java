/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Eval.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@BotCommand("eval")
public class Eval implements Command {

	private static final ScriptEngine se;
	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	private static final Logger LOG=LoggerFactory.getLogger(Eval.class);
	
	static {
		se = new ScriptEngineManager().getEngineByName("Nashorn");
		se.put("System", System.class);
        try {
			se.eval("System=System.static");
		} catch (ScriptException e) {
			LOG.warn("An Exception occurred while setting up System in eval", e);
		}
	}
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event);	
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
        se.put("event", event);
        se.put("jda", event.getJDA());
        se.put("guild", event.getGuild());
        se.put("channel", event.getChannel());
        se.put("message", event.getMessage());

        StringBuilder scriptBuilder = new StringBuilder();
        for (String string : args) {
			scriptBuilder.append(string).append(" ");
		}
		try {
			onSuccess(se.eval(scriptBuilder.toString()),event);
		} catch (ScriptException|RuntimeException e) {
			se.put(LATEST_EXCEPTION_KEY_NAME, e);
			onError(e,event);
		}
	}
	
	protected void onSuccess(Object result,GuildMessageReceivedEvent event) {
		if (result != null) {
        	event.getChannel().sendMessage("```js\n"+result.toString()+"\n```").queue();
		} else {
			event.getChannel().sendMessage("`undefined` or `null`").queue();
		}
	}
	protected void onError(Exception e,GuildMessageReceivedEvent event) {
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
			LOG.error("Error within incorrect user input/eval execution error handling",e);
		}
	}
	@Override
	public String help() {
		return null;
	}

}
