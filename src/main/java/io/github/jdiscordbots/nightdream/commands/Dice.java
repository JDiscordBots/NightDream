/*
 * Copyright (c) JDiscordBots 2019
 * File: Dice.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

@BotCommand("dice")
public class Dice implements Command {
    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" Not enough arguments!").queue();
            return;
        }
        long max=Long.parseLong(args[0]);
        if(max<1) {
        	max--;
        }
        final long l;
        try {
            l = (long)(Math.floor(Math.random() * max + 1));
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" argument needs to be an integer!").queue();
            return;
        }
        
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("Rolling the dice...")
                .setDescription(String.format("From 1 to %s", args[0]));

        event.getChannel().sendMessage(eb.build()).queue(msg->{
        	eb.clear();
            eb.setColor(0x212121).setTitle("Done!").setDescription("It landed on a " + l);
            msg.editMessage(eb.build()).queueAfter(Long.parseLong(String.valueOf(GeneralUtils.getRandInt(6))), TimeUnit.SECONDS);
        });
        
    }

    @Override
    public String help() {
        return "Rolls a random number from one";
    }
    
    @Override
    public CommandType getType() {
    	return CommandType.UTIL;
    }
}
