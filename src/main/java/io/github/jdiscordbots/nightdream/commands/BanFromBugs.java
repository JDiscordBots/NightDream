package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("banfrombugs")
public class BanFromBugs implements Command {

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event, args!=null);
	}
	@Override
	public String permNeeded() {
		return "Bot-Admin";
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length<2) {
			event.getChannel().sendMessage(IconChooser.getArrowIcon(event.getChannel())+" Format: `"+BotData.getPrefix(event.getGuild())+"banfrombugs [add,remove] userID`").queue();
			return;
		}
		String uID=args[1];
		switch(args[0]) {
		case "add":
			BotData.STORAGE.write("bugs", uID, "banned");
			event.getChannel().sendMessage("Done").queue();
			break;
		case "remove":
			BotData.STORAGE.remove("bugs", uID);
			event.getChannel().sendMessage("Done").queue();
			break;
		default:
			event.getChannel().sendMessage(IconChooser.getArrowIcon(event.getChannel())+" Format: `"+BotData.getPrefix(event.getGuild())+"-banfrombugs [add,remove] userID`").queue();
		}
	}

	@Override
	public String help() {
		return "Ban a user from bug reporting.";
	}

	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
