package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;

public class ProfileTest {

	@Test
	public void testDefaultProfile() {
		sendCommand("profile");
	}
	
	@Test
	public void testHelp() {
		assertEquals("Shows & manages your profile, `profile help` for more", new Profile().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.FUN, new Profile().getType());
	}
}
