package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

@BotCommand("choose")
public class Choose implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length <= 1) {
            Utils.errmsg(event.getTextChannel(), "<IconX:55386311960748044> Please give me some options!");
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        Random r = new Random();

        Collections.addAll(list, args);

        String chosen = args[r.nextInt(list.size())];

        EmbedBuilder eb = new EmbedBuilder().setColor(0x212121).setTitle("I've chosen!")
                .setDescription(chosen);

        Utils.msg(event.getTextChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Chooses an option from a list";
    }
}
