package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class YNTest {

	@Test
	public void testExecution() {
		sendCommand("yn");
		Message resp=getMessage(msg->hasEmbed(msg, embed->"Yes!".equals(embed.getTitle())||"No!".equals(embed.getTitle())));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getImage().getUrl().startsWith("https://yesno.wtf/assets/"+embed.getTitle().substring(0, embed.getTitle().length()-1).toLowerCase())));
		resp.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Answer a yes/no question",new YN().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.IMAGE, new YN().getType());
	}
}
