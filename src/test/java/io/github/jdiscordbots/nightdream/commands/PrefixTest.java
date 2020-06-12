/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: PrefixTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class PrefixTest {
	//allow executed cannot be tested properly...
	
	@Test
	public void testWithoutArguments() {
		sendCommand("prefix");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" I need a prefix to begin with."));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testResetPrefix() {
		BotData.setPrefix(getTestingChannel().getGuild(), "test/");
		sendMessage("test/prefix reset");
		Message resp=getMessage("Prefix reset.");
		assertNotNull(resp);
		resp.delete().queue();
		assertEquals(BotData.getDefaultPrefix(), BotData.getPrefix(getTestingChannel().getGuild()));
	}
	@Test
	public void testSetPrefix() {
		sendCommand("prefix test+");
		Message resp=getMessage("Prefix is `test+` now.");
		assertNotNull(resp);
		resp.delete().queue();
		assertEquals("test+", BotData.getPrefix(getTestingChannel().getGuild()));
		BotData.resetPrefix(getTestingChannel().getGuild());
	}
	@Test
	public void testHelp() {
		assertEquals("Sets the prefix", new Prefix().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.CONFIG, new Prefix().getType());
	}
}
