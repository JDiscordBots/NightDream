package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@BotCommand("choose")
public class Choose implements Command {

	private Random r=new Random();
	
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length <= 1) {
            JDAUtils.errmsg(event.getTextChannel(), "<IconX:55386311960748044> Please give me some options!");
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, args);

        String chosen = args[r.nextInt(list.size())];

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("I've chosen!")
                .setDescription(chosen);

        JDAUtils.msg(event.getTextChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Chooses an option from a list";
    }
}
