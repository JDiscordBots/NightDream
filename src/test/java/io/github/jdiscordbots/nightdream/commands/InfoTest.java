/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: InfoTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class InfoTest {
	
	@Test
	public void testInfo() {
		sendCommand("info");
		Message resp=getMessage(msg->msg.getMentionedUsers().size()==1&&
				msg.getMentionedUsers().get(0).equals(getJDA().getSelfUser())&&
				msg.getAttachments().size()==1&&
				msg.getAttachments().get(0).isImage());
		assertNotNull(resp);
		resp.delete().queue();
	}

	@Test
	public void testHelp() {
		assertEquals("Displays bot information", new Info().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Info().getType());
	}
}
