package io.github.bynoobiyt.nightdream.commands;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("ping")
public class Ping implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		Message msg = event.getChannel().sendMessage(":stopwatch:").complete();
		long ms= getMilliSeconds(msg.getTimeCreated()) - getMilliSeconds(event.getMessage().getTimeCreated());
		msg.editMessage("<:IconThis:553869005820002324> Latency: " + ms + "ms. API Latency is " + event.getJDA().getGatewayPing() + "ms").queue();
	}
	private static long getMilliSeconds(OffsetDateTime time) {
		return time.atZoneSameInstant(ZoneId.of("Z")).toInstant().toEpochMilli();
	}

	@Override
	public String help() {
		return "Pings!";
	}

}
