/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: MvnTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;


public class MvnTest {
	
	@Test
	public void testWithoutArgs() {
		sendCommand("mvn");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" I need a package name"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testInvalidPackage() {
		sendCommand("mvn thisisinvalid");
		Message resp=getMessage(msg->hasEmbedField(msg, field->field.getName().endsWith(" Nothing found")&&
				field.getValue().equals("Try something different.")));
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testValidPackage() {
		sendCommand("mvn nightdream-logging");
		Message resp=getMessage(msg->hasEmbed(msg, "Result",null));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "Group ID","`io.github.jdiscordbots`"));
		assertTrue(hasEmbedField(resp, "Artifact ID","nightdream-logging"));
		assertTrue(hasEmbedField(resp, embed->"Current Version".equals(embed.getName())));
		assertTrue(hasEmbedField(resp, "Repository","central"));
		resp.delete().queue();
	}
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new MVN().getType());
	}
	@Test
	public void testHelp() {
		assertEquals("Allows you to view info about a maven artifact", new MVN().help());
	}

}
