/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Rate.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

@BotCommand("rate")
public class Rate implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        int i = GeneralUtils.getRandInt(1, 100);
        User mentioned;
        if(event.getMessage().getMentionedMembers().isEmpty()) {
        	mentioned = event.getAuthor();
        }else {
        	mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
        }
        
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white)
                .setTitle(String.format("Rating %s", mentioned.getName()))
                .setDescription(String.format("%s/100", i));
        JDAUtils.msg(event.getChannel(), eb.build());
    }

    @Override
    public String help() {
        return "Rates a User";
    }
    
    @Override
    public CommandType getType() {
    	return CommandType.FUN;
    }
}
