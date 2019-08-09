package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.core.NightDream;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IFakeable;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

@BotCommand("bugreport")
public class BugReport implements Command {

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		int latestBugId = BotData.getBugID();

		int thisId = latestBugId + 1;

		BotData.setBugID(thisId);
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string).append(" ");
		}
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("New Bug").setDescription(sb.toString())
				.setFooter(event.getAuthor().getAsTag() + " | Bug ID " + thisId);

		NightDream.getJDA().getTextChannelById(BotData.getBugReportChannel()).sendMessage(eb.build()).queue();

		event.getChannel().sendMessage("Send with ID " + thisId).queue();
	}

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		if (BotData.getBugReportChannel() == null) {
			BotData.setBugReportChannel("");
			System.out.println("Bug report command is disabled. To enable it, please insert a valid channel id into NightDream.properties.");
			return false;
		}
		try {
			TextChannel channel = NightDream.getJDA().getTextChannelById(BotData.getBugReportChannel());
		} catch (Exception e) {
			System.out.println("Bug report command is disabled. To enable it, please insert a valid channel id into NightDream.properties.");
			return false;
		}
		return true;

	}

	@Override
	public String help() {
		return "Files a bug report";
	}
}
