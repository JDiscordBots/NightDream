package io.github.jdiscordbots.nightdream.commands.ksoft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NekoTest extends AbstractKSoftImageTest{
	@Override
	protected String getName() {
		return "neko";
	}
	@Test
	public void testHelp() {
		assertEquals("Sends a neko image", new Neko().help());
	}
	@Override
	protected String getInfo() {
		return "Here's a neko~";
	}
}
