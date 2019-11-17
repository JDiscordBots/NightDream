package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getPrefix;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class ActivityTest {
	@Test
	public void testWithoutArgs() {
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->msg.getContentRaw().equals(getPrefix()+"help | ")));
		sendCommand("activity");
		Message msg=getMessage("Done: "+getPrefix()+"help |");
		assertNotNull(msg);
		msg.delete().queue();
	}
	
	@Test
	public void testWithArgs() {
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->msg.getContentRaw().equals(getPrefix()+"help | ")));
		sendCommand("activity doing feature tests");
		Message msg=getMessage("Done: "+getPrefix()+"help | doing feature tests");
		assertNotNull(msg);
		msg.delete().queue();
	}
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.CONFIG, new Activity().getType());
	}
	
	@Test
	public void testHelp() {
		assertEquals("Changes the bot's activity", new Activity().help());
	}
}
