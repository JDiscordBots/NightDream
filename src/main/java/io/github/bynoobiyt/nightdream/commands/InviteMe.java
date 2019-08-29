/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: InviteMe.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@BotCommand("inviteme")
public class InviteMe implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("Invites")
                .setDescription(String.format("[Add the bot](https://discordapp.com/api/oauth2/authorize?client_id=%s&permissions=8&scope=bot)%n"
                		+ "[Server invite](https://discordapp.com/invite/KjMsK5G)", event.getJDA().getSelfUser().getId()));
        JDAUtils.msg(event.getChannel(), eb.build());
    }

    @Override
    public String help() {
        return "Invites the bot";
    }
}
