/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: AbstractAdminCommandTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public abstract class AbstractAdminCommandTest {
	
	private static final Logger LOG=LoggerFactory.getLogger(AbstractAdminCommandTest.class);
	
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
		LOG.debug("Admins before non-admin test: "+Arrays.toString(BotData.getAdminIDs()));
		String[] adminIDs=BotData.getAdminIDs();
		BotData.setAdminIDs(Stream.of(adminIDs).filter(id->!id.equals(getJDA().getSelfUser().getId())).toArray(String[]::new));
		sendCommand(cmdName());
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" This is an admin command."));
		LOG.debug("Admins during non-admin test: "+Arrays.toString(BotData.getAdminIDs()));
		assertNotNull(resp);
		resp.delete().queue();
		BotData.setAdminIDs(adminIDs);
		LOG.debug("reset admins: "+Arrays.toString(BotData.getAdminIDs()));
	}
	protected abstract String cmdName();
	protected abstract Command cmd();
}
