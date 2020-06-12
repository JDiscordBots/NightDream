/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: ChooseTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class ChooseTest {
	@Test
	public void testMissingOptions() {
		Predicate<Message> tester=msg->msg.getContentRaw().endsWith("Please give me some options!");
		assertNull(getAlreadySentMessage(getTestingChannel(), tester));
		sendCommand("choose");
		Message msg=getMessage(tester);
		assertNotNull(msg);
		msg.delete().complete();
		sendCommand("choose a");
		msg=getMessage(tester);
		assertNotNull(msg);
		msg.delete().complete();
	}
	@Test
	public void testCorrectUse() {
		Predicate<Message> tester=msg->hasEmbed(msg, "I've chosen!", "a")||hasEmbed(msg, "I've chosen!", "b");
		assertNull(getAlreadySentMessage(getTestingChannel(), tester));
		sendCommand("choose a b");
		Message msg=getMessage(tester);
		assertNotNull(msg);
		msg.delete().complete();
	}
	@Test
	public void testHelp() {
		assertEquals("Chooses an option from a list", new Choose().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.FUN,new Choose().getType());
	}
}
