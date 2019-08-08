package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import static net.dv8tion.jda.api.entities.Activity.playing;

@BotCommand("activity")
public class Activity implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessageFormat("%s, Something went very wrong! <:IconX:553868311960748044>", event.getMember().getAsMention()).queue();
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }
        BotData.setGlobalProperty("game", builder.toString());
        builder.insert(0, BotData.getDefaultPrefix() + " | " );
        String gameName=builder.toString();
        event.getJDA().getPresence().setActivity(playing(gameName));
        
        event.getChannel().sendMessage(gameName).queue();
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public boolean allowExecute(String[] args, MessageReceivedEvent event) {
        return Utils.checkOwner(event);
    }
}
