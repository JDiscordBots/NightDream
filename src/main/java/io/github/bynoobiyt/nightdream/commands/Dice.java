package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BotCommand("dice")
public class Dice implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessage("<:IconProvide:553870022125027329> Not enough arguments!").queue();
            return;
        }
        Random r = new Random();
        double d = 0;
        try {
            d = Math.floor(Math.random() * Double.parseDouble(args[0]) + 1);
        } catch (NumberFormatException e) {
            JDAUtils.errmsg(event.getTextChannel(), "<:IconProvide:553870022125027329> Not enough arguments!");
            return;
        }
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("Rolling the dice...")
                .setDescription(String.format("From 1 to %s", args[0]));

        Message msg = JDAUtils.msg(event.getTextChannel(), eb.build(), false);

        eb.clear();

        eb.setColor(0x212121).setTitle("Done!").setDescription("It landed on a " + d);

        msg.editMessage(eb.build()).completeAfter(Long.parseLong(String.valueOf(r.nextInt(6))), TimeUnit.SECONDS);
    }

    @Override
    public String help() {
        return "Rolls a random number from one";
    }
}
