package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("msglog")
public class MsgLog implements Command {
	
	private static final String NEED_MENTIONED_CHANNEL="<:IconProvide:553870022125027329> I need a mentioned channel";

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if (args.length == 0) {
			event.getChannel().sendMessage(NEED_MENTIONED_CHANNEL).queue();
			return;
		}
		if (args[0].equals("none")) {
			BotData.resetMsgLogChannel(event.getGuild());
			event.getChannel().sendMessage("Removed").queue();
			return;
		}
		if (event.getMessage().getMentionedChannels().isEmpty()) {
			event.getChannel().sendMessage(NEED_MENTIONED_CHANNEL).queue();
			return;
		}
		TextChannel channel = null;
		try {
			channel = event.getMessage().getMentionedChannels().get(0);
		} catch (Exception e) {
			event.getChannel().sendMessage(NEED_MENTIONED_CHANNEL).queue();
			return;
		}
		BotData.setMsgLogChannel(channel.getId(), event.getGuild());
		event.getChannel().sendMessage("Set! `" + BotData.getPrefix(event.getGuild()) + "msglog none` to disable.").queue();
	}

	@Override
	public String help() {
		return "Sets up a log channel for deleted messages";
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || JDAUtils.checkOwner(event);
	}
}
