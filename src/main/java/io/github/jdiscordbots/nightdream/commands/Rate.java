/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Rate.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

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
}