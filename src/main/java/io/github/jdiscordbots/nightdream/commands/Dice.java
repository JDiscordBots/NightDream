/*
 * Copyright (c) JDiscordBots 2019
 * File: Dice.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@BotCommand("dice")
public class Dice implements Command {
    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessage("<:IconProvide:553870022125027329> Not enough arguments!").queue();
            return;
        }
        double d = 0;
        try {
            d = Math.floor(Math.random() * Double.parseDouble(args[0]) + 1);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("<:IconProvide:553870022125027329> Not enough arguments!").queue();
            return;
        }
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("Rolling the dice...")
                .setDescription(String.format("From 1 to %s", args[0]));

        Message msg = JDAUtils.msg(event.getChannel(), eb.build());

        eb.clear();

        eb.setColor(0x212121).setTitle("Done!").setDescription("It landed on a " + d);

        msg.editMessage(eb.build()).completeAfter(Long.parseLong(String.valueOf(GeneralUtils.getRandInt(6))), TimeUnit.SECONDS);
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
