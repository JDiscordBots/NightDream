package io.github.jdiscordbots.nightdream.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;

public class MvnTest {
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new MVN().getType());
	}
	@Test
	public void testHelp() {
		assertEquals("Allows you to view info about a maven artifact", new MVN().help());
	}

}
