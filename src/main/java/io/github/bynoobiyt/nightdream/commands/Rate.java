package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.Utils;
import jdk.jshell.execution.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

@BotCommand("rate")
public class Rate implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Random r = new Random();
        int i = r.nextInt(100) + 1;
        Member mentioned = null;
        try {
            mentioned = event.getMessage().getMentionedMembers().get(0);
        } catch (Exception ignored) {
            // mentioned = event.getMember();
        }

        EmbedBuilder eb = new EmbedBuilder().setColor(0x212121)
                .setTitle(String.format("Rating %s", /* mentioned.getUser().getName() */ mentioned != null ? mentioned.getUser().getName() : event.getAuthor().getName()))
                .setDescription(String.format("%s/100", i));
        Utils.msg(event.getTextChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Rates a User";
    }
}
