package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.core.NightDream;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@BotCommand("bugreport")
public class BugReport implements Command {

	private static final Logger LOG=LoggerFactory.getLogger(BugReport.class);
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
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
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		if (BotData.getBugReportChannel() == null) {
			BotData.setBugReportChannel("");
			LOG.warn("Bug report command is disabled. To enable it, please insert a valid channel id into NightDream.properties.");
			return false;
		}
		try {
			NightDream.getJDA().getTextChannelById(BotData.getBugReportChannel());
		} catch (Exception e) {
			LOG.warn("Bug report command is disabled. To enable it, please insert a valid channel id into NightDream.properties.");
			return false;
		}
		return true;

	}

	@Override
	public String help() {
		return "Files a bug report";
	}
}
