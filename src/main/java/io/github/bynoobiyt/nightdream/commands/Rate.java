package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;

@BotCommand("rate")
public class Rate implements Command {

	private Random r = new Random();
	
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        int i = r.nextInt(100) + 1;
        User mentioned = null;
        try {
            mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
        } catch (Exception ignored) {
        	//ignore
        }

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white)
                .setTitle(String.format("Rating %s", mentioned != null ? mentioned.getName() : event.getAuthor().getName()))
                .setDescription(String.format("%s/100", i));
        Utils.msg(event.getTextChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Rates a User";
    }
}
