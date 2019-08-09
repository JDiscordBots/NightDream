package io.github.bynoobiyt.nightdream.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

@BotCommand("avatar")
public class Avatar implements Command {

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		User user;
		try {
			user = event.getMessage().getMentionedMembers().get(0).getUser();
		} catch (Exception e) {
			user = event.getAuthor();
		}

		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setImage(user.getAvatarUrl() + "?size=2048");
		event.getChannel().sendMessage(eb.build()).queue();
	}

	@Override
	public String help() {
		return "Shows your (or someone else's) Avatar";
	}
}
