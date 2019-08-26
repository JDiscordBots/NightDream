package io.github.bynoobiyt.nightdream.commands;


import java.util.Timer;
import java.util.TimerTask;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("seval")
public class SEval implements Command {

	private ScriptEngine se;
	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	
	private static final Logger LOG=LoggerFactory.getLogger(SEval.class);
	
	public SEval() {
		se = new ScriptEngineManager().getEngineByName("Nashorn");
		se.put("System", System.class);
        try {
			se.eval("System=System.static");
		} catch (ScriptException e) {
			LOG.warn("An Exception occured while setting up System in seval",e);
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
			msg.addReaction("\u274C").queue();
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					Message message = event.getChannel().retrieveMessageById(msg.getId()).complete();
					for (MessageReaction reaction : message.getReactions()) {
						if("\u274C".equals(reaction.getReactionEmote().getEmoji()) && reaction.retrieveUsers().complete().contains(event.getAuthor())) {
							message.delete().queue();
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
