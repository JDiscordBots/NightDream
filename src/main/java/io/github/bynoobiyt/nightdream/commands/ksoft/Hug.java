package io.github.bynoobiyt.nightdream.commands.ksoft;

import io.github.bynoobiyt.nightdream.commands.BotCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.enums.ImageTag;

@BotCommand("hug")
public class Hug extends KSoftImageCommand {

	private String executor=null;
	private String target=null;
	
	@Override
	public String help() {
		return "Hugs someone or yourself :)";
	}

	@Override
	public synchronized void action(String[] args, GuildMessageReceivedEvent event) {
		executor=event.getMember().getEffectiveName();
		if(event.getMessage().getMentionedMembers().isEmpty()) {
			target=executor;
		}else{
			target=event.getMessage().getMentionedMembers().get(0).getEffectiveName();
		}	
		super.action(args, event);
	}
	
	@Override
	protected synchronized String getTitle() {
		return "**"+target+"** has been hugged by **"+executor+"**!";
	}

	@Override
	protected ImageTag getImageTag() {
		return ImageTag.HUG;
	}

}
