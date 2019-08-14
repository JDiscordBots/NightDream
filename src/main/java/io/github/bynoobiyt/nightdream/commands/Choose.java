package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@BotCommand("choose")
public class Choose implements Command {

	private Random r=new Random();
	
    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        if (args.length <= 1) {
            JDAUtils.errmsg(event.getChannel(), "<IconX:55386311960748044> Please give me some options!");
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, args);

        String chosen = args[r.nextInt(list.size())];

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("I've chosen!")
                .setDescription(chosen);

        JDAUtils.msg(event.getChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Chooses an option from a list";
    }
}
