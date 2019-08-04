package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

@BotCommand("rate")
public class Rate implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Random r = new Random();
        int i = r.nextInt(100) + 1;
        User mentioned = null;
        try {
            mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
        } catch (Exception ignored) {
        }

        EmbedBuilder eb = new EmbedBuilder().setColor(0x212121)
                .setTitle(String.format("Rating %s", mentioned != null ? mentioned.getName() : event.getAuthor().getName()))
                .setDescription(String.format("%s/100", i));
        Utils.msg(event.getTextChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Rates a User";
    }
}
