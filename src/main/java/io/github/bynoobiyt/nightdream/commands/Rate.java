package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Random;

@BotCommand("rate")
public class Rate implements Command {

	private Random r = new Random();
	
    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        int i = r.nextInt(100) + 1;
        User mentioned;
        try {
            mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
        } catch (Exception ignored) {
            mentioned = event.getAuthor();
        }

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white)
                .setTitle(String.format("Rating %s", mentioned.getName()))
                .setDescription(String.format("%s/100", i));
        JDAUtils.msg(event.getChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Rates a User";
    }
}
