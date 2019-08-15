package io.github.bynoobiyt.nightdream.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("ping")
public class Ping implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription(":stopwatch:");
		builder.setColor(Color.white);
		Message msg = JDAUtils.msg(event.getChannel(), builder.build());
		builder.setColor(Color.GREEN);
		System.out.println();
		long ms= getMilliSeconds(msg.getTimeCreated()) - getMilliSeconds(event.getMessage().getTimeCreated());
		builder.setDescription("<:IconThis:553869005820002324> Latency: " + ms + "ms. API Latency is " + event.getJDA().getGatewayPing() + "ms");
		msg.editMessage(builder.build()).queue();
	}
	private long getMilliSeconds(OffsetDateTime time) {
		return time.atZoneSameInstant(ZoneId.of("Z")).toInstant().toEpochMilli();
	}

	@Override
	public String help() {
		return "Pings!";
	}

}
