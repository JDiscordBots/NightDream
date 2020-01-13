package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;


import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;


public class PingTest {
	@Test
	public void testPing() {
		sendCommand("ping");
		Message resp=getMessage(":stopwatch:");
		assertNotNull(resp);
		Message edited=getMessage(msg->msg.getContentRaw().matches(".+ Latency: \\d+ms. API Latency is \\d+ms"));
		assertNotNull(edited);
		assertEquals(resp.getId(), edited.getId());
		edited.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Pings!", new Ping().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Ping().getType());
	}
}
