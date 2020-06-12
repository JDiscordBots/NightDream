/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: GitHubTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class GitHubTest {
	@BeforeAll
	public static void setUp() {
		getJDA();
	}
	
	@Test
	public void testWithoutArguments() {
		sendCommand("github");
		Message resp=getMessage(msg->hasEmbed(msg, "JDiscordBots/NightDream","a Clone of the Discord Bot Daydream (https://gitlab.com/botstudio/daydream) in Java"));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "Language","Java"));
		assertTrue(hasEmbed(resp, embed->embed.getAuthor()!=null));
		assertTrue(hasEmbed(resp, embed->embed.getAuthor().getIconUrl()==null));
		testFooter(resp);
		resp.delete().queue();
	}
	private void testFooter(Message msg) {
		assertTrue(hasEmbed(msg, embed->embed.getFooter()!=null));
		assertTrue(hasEmbed(msg, embed->"Powered by github.com (logically)".equals(embed.getFooter().getText())));
	}
	@Test
	public void testWithUser() {
		sendCommand("github octocat");
		Message resp=getMessage(msg->hasEmbed(msg, "User `octocat`",null));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "Company","GitHub"));
		assertTrue(hasEmbed(resp, embed->embed.getAuthor()!=null));
		assertTrue(hasEmbed(resp, embed->embed.getAuthor().getIconUrl()!=null));
		testFooter(resp);
		resp.delete().queue();
	}
	public void testWithOrg() {
		sendCommand("github JDiscordBots");
		Message resp=getMessage(msg->hasEmbed(msg, "Organization `JDiscordBots`",null));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getAuthor()!=null));
		assertTrue(hasEmbed(resp, embed->embed.getAuthor().getIconUrl()!=null));
		testFooter(resp);
		resp.delete().queue();
	}
	@Test
	public void testHelp() throws IOException {
		assertEquals("Query GitHub without actually visiting it", new GitHub().help());
	}
	@Test
	public void testCommandType() throws IOException {
		assertEquals(CommandType.UTIL, new GitHub().getType());
	}
}
