package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.core.CommandHandler;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@BotCommand("help")
public class Help implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder builder=new EmbedBuilder().setColor(Color.white);
		builder.setTitle("Nightdream Commands");
		CommandHandler.getCommands().forEach((k,v)->{
			String help=v.help();
			if(help!=null) {
				builder.addField(new Field(k, v.help(), true));
			}
		});
		JDAUtils.msg(event.getChannel(), builder.build(),false);
	}

	@Override
	public String help() {
		return "¯\\_(ツ)_/¯";
	}

}
