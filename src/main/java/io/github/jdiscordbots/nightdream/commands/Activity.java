/*
 * Copyright (c) JDiscordBots 2019
 * File: Activity.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import static net.dv8tion.jda.api.entities.Activity.playing;

@BotCommand("activity")
public class Activity implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {

        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }
        BotData.setGame(builder.toString());
        builder.insert(0, BotData.getDefaultPrefix() + "help | " );
        String gameName = builder.toString();
        event.getJDA().getPresence().setActivity(playing(gameName));
        
        event.getChannel().sendMessage("Done: " + gameName).queue();
    }

    @Override
    public String help() {
        return "Changes the bot's activity";
    }

    @Override
    public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
        return JDAUtils.checkOwner(event,args!=null);
    }
    
    @Override
    public String permNeeded() {
    	return "Bot-Admin";
    }
    
    @Override
    public CommandType getType() {
    	return CommandType.CONFIG;
    }
}
