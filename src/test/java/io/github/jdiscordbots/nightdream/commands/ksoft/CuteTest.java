package io.github.jdiscordbots.nightdream.commands.ksoft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CuteTest extends AbstractKSoftImageTest{
	@Override
	protected String getName() {
		return "cute";
	}
	@Test
	public void testHelp() {
		assertEquals("Shows you a cute picture... Aww :3", new Cute().help());
	}
	@Override
	protected String getInfo() {
		return "Here's a cute dog :3";
	}
}
