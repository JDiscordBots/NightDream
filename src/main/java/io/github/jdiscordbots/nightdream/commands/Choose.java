/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Choose.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

@BotCommand("choose")
public class Choose implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        if (args.length <= 1) {
            event.getChannel().sendMessage(IconChooser.getErrorIcon(event.getChannel())+" Please give me some options!").queue();
            return;
        }
        String chosen = args[GeneralUtils.getRandInt(args.length)];

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("I've chosen!")
                .setDescription(chosen);

        JDAUtils.msg(event.getChannel(), eb.build());
    }

    @Override
    public String help() {
        return "Chooses an option from a list";
    }
    @Override
    public CommandType getType() {
    	return CommandType.FUN;
    }
}
