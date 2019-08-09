package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.core.NightDream;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

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
        if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EXT_EMOJI)) {
            event.getChannel().sendMessageFormat("In %s guilds, serving %s users and %s bots.\r\n" +
                    "This instance is owned by dan1st#7327 and Gehasstes#0001.\r\n" +
                    "JDA v4.BETA.0_32\r\n" +
                    "Logo Font: Avenir Next LT Pro / (c) Linotype\r\n" +
                    "(c) dan1st and Gehasstes %s, Release %s.", event.getJDA().getGuilds().size(), getUsers(), getBots(), Year.now().getValue(), NightDream.VERSION).queue();
        }
    }

    @Override
    public String help() {
        return "Displays bot information";
    }
}
