package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.core.NightDream;
import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.TextToGraphics;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BotCommand("info")
public class Info implements Command {

    private static int getUsers() {
        List<User> list = new ArrayList<>();
        NightDream.getJDA().getUsers().forEach(user -> {
            if (!user.isBot()) list.add(user);
        });
        return list.size();
    }

    private static int getBots() {
        List<User> list = new ArrayList<>();
        NightDream.getJDA().getUsers().forEach(user -> {
            if (user.isBot()) list.add(user);
        });
        return list.size();
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
    	final JDA jda=event.getJDA();
        if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EXT_EMOJI)) {
        	TextToGraphics.sendTextAsImage(event.getTextChannel(), "info.png", String.format(
        			"Bot Info\n" +
        			"\tIn %s guilds, serving %s users and %s bots.\n" +
                            "\tThis instance is owned by " + Stream.of(BotData.getAdminIDs()).map(id ->jda.retrieveUserById(id).complete().getAsTag()).collect(Collectors.joining(" and ")) + ".\n" +
                            "\tJDA v4.0.0_39\n" +
                            "\tLogo Font: Avenir Next LT Pro / (c) Linotype\n" +
                            "\t(c) dan1st and Gehasstes %s, Release %s.\n ", event.getJDA().getGuilds().size(), getUsers(), getBots(), Year.now().getValue(), NightDream.VERSION
        			), event.getAuthor().getAsMention());
        }
    }

    @Override
    public String help() {
        return "Displays bot information";
    }
}
