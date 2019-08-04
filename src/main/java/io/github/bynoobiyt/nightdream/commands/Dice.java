package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BotCommand("dice")
public class Dice implements Command {

	private Random r=new Random();
	
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args[0] == null) {
            event.getChannel().sendMessage("<:IconProvide:553870022125027329> Not enough arguments!").queue();
            return;
        }
        EmbedBuilder eb = new EmbedBuilder().setColor(0x212121).setTitle("Rolling the dice...")
                .setDescription(String.format("From 1 to %s", args[0]));

        Message msg = Utils.msg(event.getTextChannel(), eb.build(), false);

        eb.clear();

        eb.setColor(0x212121).setTitle("Done!").setDescription("It landed on a " + Math.floor(Math.random() * Double.parseDouble(args[0]) + 1));

        msg.editMessage(eb.build()).completeAfter(Long.parseLong(String.valueOf(r.nextInt(6))), TimeUnit.SECONDS);
    }

    @Override
    public String help() {
        return "Rolls a random number from one";
    }
}
