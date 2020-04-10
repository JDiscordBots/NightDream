package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.JDALoader;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class RateTest {

	@Test
	public void testWithoutArgs() {
		sendCommand("rate");
		Message resp=getMessage(msg->hasEmbed(msg,embed->("Rating "+getJDA().getSelfUser().getName()).equals(embed.getTitle())));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->"/100".equals(embed.getDescription().substring(2))));
		assertTrue(hasEmbed(resp, embed->Integer.parseInt(embed.getDescription().substring(0, 2))>0));
		resp.delete().queue();
	}
	@Test
	public void testWithMention() {
		Member toTag=JDALoader.getTestUser();
		sendCommand("rate "+toTag.getAsMention());
		Message resp=getMessage(msg->hasEmbed(msg,embed->("Rating "+toTag.getUser().getName()).equals(embed.getTitle())));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->"/100".equals(embed.getDescription().substring(2))));
		assertTrue(hasEmbed(resp, embed->Integer.parseInt(embed.getDescription().substring(0, 2))>0));
		resp.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Rates a User", new Rate().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.FUN, new Rate().getType());
	}
}
