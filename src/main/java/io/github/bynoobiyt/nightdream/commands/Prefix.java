package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("prefix")
public class Prefix implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessageFormat("%s, Something went very wrong! <:IconX:553868311960748044>", event.getMember().getAsMention()).queue();
            return;
        }
        if (args[0].equals("reset")) {
            event.getChannel().sendMessage("Prefix reset.").queue();
            BotData.resetPrefix(event.getGuild());
        } else {
            event.getChannel().sendMessageFormat("Prefix is `%s` now.", args[0].toLowerCase()).queue();
            BotData.setPrefix(event.getGuild(), args[0].toLowerCase());
        }
    }

    @Override
    public boolean allowExecute(String[] args, MessageReceivedEvent event) {
        return event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().hasPermission(Permission.MANAGE_SERVER) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE);
    }

    @Override
    public String help() {
        return "Sets the prefix";
    }
}