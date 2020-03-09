/*
 * Copyright (c) JDiscordBots 2020
 * File: BioTest.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.GeneralUtils;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.entities.Message;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.github.jdiscordbots.jdatesting.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class BioTest {

	@Test
	public void testWithoutArguments() {
		sendCommand("bio");
		Message response = getMessage(IconChooser.getErrorIcon(getTestingChannel()) + " This user does not exist on https://discord.bio. Try again with another username.");
		assertNotNull(response);
		response.delete().queue();
	}

	@Test
	public void testWithInvalidArgument() {
		sendCommand("bio thisisinvalid");
		Message response = getMessage(IconChooser.getErrorIcon(getTestingChannel()) + " This user does not exist on https://discord.bio. Try again with another username.");
		assertNotNull(response);
		response.delete().queue();
	}

	@Test
	public void testWithValidArgument() {
		final JSONObject settings = GeneralUtils.getJSONFromURL("https://api.discord.bio/v1/userdetails/358291050957111296").getJSONObject("payload").getJSONObject("settings");
		assertNotNull(settings);
		assertFalse(settings.isNull("status"));
		assertFalse(settings.isNull("user_id"));
		sendCommand("bio dan1st");
		Message response = getMessage(msg -> hasEmbed(msg, null, settings.getString("status")));
		assertTrue(hasEmbedField(response, "ID", settings.getString("user_id")));
		assertTrue(hasEmbedField(response, "Verified", settings.getString("verified")));
	}

	@Test
	public void testCommandType() {
		assertSame(Command.CommandType.UTIL, new Bio().getType());
	}

	@Test
	public void testHelp() {
		assertEquals("The first Discord-based discord.bio UI in Java", new Bio().help());
	}
}