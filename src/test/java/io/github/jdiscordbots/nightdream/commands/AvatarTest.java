package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Member;

public class AvatarTest {
	@Test
	public void testWithoutArgs() {
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->hasEmbed(msg, embed->embed.getImage().getUrl().equals(getJDA().getSelfUser().getAvatarUrl()))));
		sendCommand("avatar");
		getMessage("You don't have an avatar...").delete().queue();
	}
	@Test
	public void testWithoutServerOwner() {
		Member owner = getTestingChannel().getGuild().getOwner();
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->hasEmbed(msg, embed->embed.getImage().getUrl().equals(owner.getUser().getAvatarUrl()))));
		sendCommand("avatar "+owner.getAsMention());
		getMessage(msg->hasEmbed(msg, embed->embed.getImage().getUrl().equals(owner.getUser().getAvatarUrl()+"?size=2048"))).delete().queue();
	}
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new Avatar().getType());
	}
	
	@Test
	public void testHelp() {
		assertEquals("Shows your (or someone else's) Avatar", new Avatar().help());
	}
}
