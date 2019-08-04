package io.github.bynoobiyt.nightdream.commands;

import java.awt.Color;

import io.github.bynoobiyt.nightdream.core.CommandHandler;
import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("help")
public class Help implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		EmbedBuilder builder=new EmbedBuilder().setColor(Color.GRAY);
		builder.setTitle("Nightdream Commands");
		CommandHandler.getCommands().forEach((k,v)->{
			String help=v.help();
			if(help!=null) {
				builder.addField(new Field(k, v.help(), true));
			}
		});
		Utils.msg(event.getTextChannel(), builder.build(),false);
	}

	@Override
	public String help() {
		return "¯\\_(ツ)_/¯";
	}

}
