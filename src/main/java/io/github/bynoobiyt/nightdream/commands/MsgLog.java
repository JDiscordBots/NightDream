package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// TODO: 08.08.2019 TEST
@BotCommand("msglog")
public class MsgLog implements Command {

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if (args.length == 0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a mentioned channel").queue();
			return;
		}
		if (args[0].equals("none")) {
			BotData.resetMsgLogChannel(event.getGuild());
			event.getChannel().sendMessage("Removed").queue();
			return;
		}
		if (event.getMessage().getMentionedChannels().isEmpty()) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a mentioned channel").queue();
			return;
		}
		TextChannel channel = null;
		try {
			channel = event.getMessage().getMentionedChannels().get(0);
		} catch (Exception e) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a mentioned channel").queue();
		}
		BotData.setMsgLogChannel(channel.getId(), event.getGuild());
		event.getChannel().sendMessage("Set! `" + BotData.getPrefix(event.getGuild()) + "msglog none` to disable.").queue();
	}

	@Override
	public String help() {
		return "Sets up a log channel for deleted messages";
	}

	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || Utils.checkOwner(event);
	}
}
