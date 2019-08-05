package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("inviteme")
public class InviteMe implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder().setColor(0x212121).setTitle("Invites")
                .setDescription(String.format("[Add the bot](https://discordapp.com/api/oauth2/authorize?client_id=%s&permissions=8&scope=bot)\n[Server invite](https://discordapp.com/invite/KjMsK5G)", event.getJDA().getSelfUser().getId()));

        Utils.msg(event.getTextChannel(), eb.build(), false);
    }

    @Override
    public String help() {
        return "Invites the bot";
    }
}
