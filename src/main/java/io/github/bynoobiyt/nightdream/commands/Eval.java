package io.github.bynoobiyt.nightdream.commands;


import java.awt.Color;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("eval")
public class Eval implements Command{

	private ScriptEngine se;
	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	
	public Eval() {
		se = new ScriptEngineManager().getEngineByName("Nashorn");
		se.put("System", System.class);
        try {
			se.eval("System=System.static");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return Utils.checkOwner(event);	
	}
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
        se.put("event", event);
        se.put("jda", event.getJDA());
        se.put("guild", event.getGuild());
        se.put("channel", event.getChannel());
        se.put("message", event.getMessage());

        StringBuilder scriptBuilder = new StringBuilder();
        for (String string : args) {
			scriptBuilder.append(string).append(" ");
		}
        Object result = null;
		try {
			result = se.eval(scriptBuilder.toString());
		} catch (ScriptException e) {
			Utils.msg(event.getTextChannel(), "" + " \n " + e.getMessage(), Color.RED,false);
			se.put(LATEST_EXCEPTION_KEY_NAME, e);
		}
        if (result != null) {
			Utils.msg(event.getTextChannel(), result.toString());
		}
	}

	@Override
	public String help() {
		return null;
	}

}
