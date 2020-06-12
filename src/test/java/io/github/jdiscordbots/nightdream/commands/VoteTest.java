/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: VoteTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class VoteTest {
	@Test
	public void testVote() {
		sendCommand("vote");
		Message resp=getMessage(msg->hasEmbed(msg, "Vote for "+getJDA().getSelfUser().getName(),"[<3](https://top.gg/bot/"+getJDA().getSelfUser().getId()+"/vote)"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("Vote for me! <3", new Vote().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Vote().getType());
	}
}
