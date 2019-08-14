package io.github.bynoobiyt.nightdream.commands;


import java.util.Timer;
import java.util.TimerTask;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("seval")
public class SEval implements Command {

	private ScriptEngine se;
	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	
	public SEval() {
		se = new ScriptEngineManager().getEngineByName("Nashorn");
		se.put("System", System.class);
        try {
			se.eval("System=System.static");
		} catch (ScriptException e) {
			e.printStackTrace();
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
			se.eval(scriptBuilder.toString());
		} catch (ScriptException e) {
			se.put(LATEST_EXCEPTION_KEY_NAME, e);
			final Message msg = event.getChannel().sendMessage("No...").complete();
			msg.addReaction("\u274C").complete();
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					Message message = event.getChannel().retrieveMessageById(msg.getId()).complete();
					for (MessageReaction reaction : message.getReactions()) {
						if(reaction.getReactionEmote().getEmoji().equals("\u274C") && reaction.retrieveUsers().complete().contains(event.getAuthor())) {
							message.delete().complete();
							return;
						}
					}
				}
			}, 60000);
		}
	}

	@Override
	public String help() {
		return null;
	}

}
