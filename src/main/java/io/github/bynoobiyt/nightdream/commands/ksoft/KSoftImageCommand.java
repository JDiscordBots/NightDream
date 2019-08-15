package io.github.bynoobiyt.nightdream.commands.ksoft;

import io.github.bynoobiyt.nightdream.commands.Command;
import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import io.github.bynoobiyt.nightdream.util.KSoftUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.explodingbush.ksoftapi.entities.TaggedImage;
import net.explodingbush.ksoftapi.enums.ImageTag;

public abstract class KSoftImageCommand implements Command {

	protected abstract String getTitle();
	protected abstract ImageTag getImageTag();
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {//TODO test
		event.getChannel().sendTyping().complete();
		TaggedImage img = KSoftUtil.getImage(getImageTag());
		if(img==null) {
			JDAUtils.errmsg(event.getChannel(), "This command is disabled due there is no KSoft API token");
			return;
		}
		EmbedBuilder builder=new EmbedBuilder();
		builder.setColor(0x212121)
		.setImage(img.getUrl())
		.setTitle(getTitle())
		.setFooter("Served by an external API - report with " + BotData.getPrefix(event.getGuild()) + "bugreport [url]");
		event.getChannel().sendMessage(builder.build()).queue();
	}
}
