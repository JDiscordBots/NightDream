package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.util.BotData;

public abstract class AbstractAdminCommandTest {
	@BeforeAll
	public static void init() {
		TestUtils.getJDA();//make sure test utils are loaded
	}
	@Test
	public void testPermissionString() {
		assertEquals("Bot-Admin", cmd().permNeeded());
	}
	@Test
	public void testNonAdmin() {
		String[] adminIDs=BotData.getAdminIDs();
		BotData.setAdminIDs(Stream.of(adminIDs).filter(id->!id.equals(getJDA().getSelfUser().getId())).toArray(String[]::new));
		sendCommand(cmdName());
		getMessage(msg->msg.getContentRaw().endsWith(" This is an admin command.")).delete().complete();
		BotData.setAdminIDs(adminIDs);
	}
	protected abstract String cmdName();
	protected abstract Command cmd();
}
