package io.github.bynoobiyt.nightdream.commands;

import java.awt.Color;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("ping")
public class Ping implements Command {

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription(":alarm_clock:");
		builder.setColor(Color.white);
		Message msg = Utils.msg(event.getTextChannel(), builder.build(),false);
		builder.setColor(Color.GREEN);
		builder.setDescription("<:IconThis:553869005820002324> Latency: " + (msg.getTimeCreated().getNano() - event.getMessage().getTimeCreated().getNano()) / 1000000 + "ms. API Latency is " + event.getJDA().getGatewayPing() + "ms");
		msg.editMessage(builder.build()).complete();
	}

	@Override
	public String help() {
		return "Pings!";
	}

}
