/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: InviteMe.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;

@BotCommand("inviteme")
public class InviteMe implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("Invites")
                .setDescription(String.format("[Add the bot](https://discord.com/api/oauth2/authorize?client_id=%s&permissions=8&scope=bot)%n"
                		+ "[Server invite](%s)", event.getJDA().getSelfUser().getId(),BotData.getSupportServer()));
        JDAUtils.msg(event.getChannel(), eb.build());
    }

    @Override
    public String help() {
        return "Invites the bot";
    }
    
    @Override
    public CommandType getType() {
    	return CommandType.META;
    }
}
