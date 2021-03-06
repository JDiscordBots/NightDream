/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Prefix.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("prefix")
public class Prefix implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" I need a prefix to begin with.").queue();
            return;
        }
        if ("reset".equals(args[0])) {
            event.getChannel().sendMessage("Prefix reset.").queue();
            BotData.resetPrefix(event.getGuild());
        } else {
            event.getChannel().sendMessageFormat("Prefix is `%s` now.", args[0].toLowerCase()).queue();
            BotData.setPrefix(event.getGuild(), args[0].toLowerCase());
        }
    }

    @Override
    public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
        return event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().hasPermission(Permission.MANAGE_SERVER) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || JDAUtils.checkOwner(event,args!=null);
    }

    @Override
    public String help() {
        return "Sets the prefix";
    }
    
    @Override
    public CommandType getType() {
    	return CommandType.CONFIG;
    }
}
