/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Activity.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
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
        builder.append(BotData.getDefaultPrefix() + "help | " );
        String input=String.join(" ", args);
        builder.append(input);
        BotData.setGame(input);
        
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
