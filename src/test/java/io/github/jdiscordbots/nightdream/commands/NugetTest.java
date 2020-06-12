/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: NugetTest.java
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

public class NugetTest {
	
	@Test
	public void testWithoutArgs() {
		sendCommand("nuget");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" I need a package name"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testInvalidPackage() {
		sendCommand("nuget thisisinvalid");
		Message resp=getMessage(msg->hasEmbed(msg, embed->embed.getTitle()!=null&&embed.getTitle().endsWith(" Nothing found")&&
				"Try something different.".equals(embed.getDescription())));
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testValidPackage() {
		sendCommand("nuget discord");
		Message resp=getMessage(msg->hasEmbed(msg, "Result",null));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getThumbnail()!=null));
		assertTrue(hasEmbedField(resp, "Name","Discord.Net"));
		assertTrue(hasEmbedField(resp, "Description","An asynchronous API wrapper for Discord. This metapackage includes all of the optional Discord.Net components."));
		assertTrue(hasEmbedField(resp, "Namespace","`Discord.Net`"));
		assertTrue(hasEmbedField(resp, embed->"Current Version".equals(embed.getName())));
		assertTrue(hasEmbedField(resp, "Authors","\"Discord.Net Contributors\""));
		assertTrue(hasEmbedField(resp, "Tags","`\"discord\", \"discordapp\"`"));
		assertTrue(hasEmbedField(resp, "Verified","false"));
		assertTrue(hasEmbedField(resp, embed->"Downloads".equals(embed.getName())));
		resp.delete().queue();
	}
	
	@Test
	public void testPackageWithoutIconOrTags() {
		sendCommand("nuget hello");
		Message resp=getMessage(msg->hasEmbed(msg, "Result",null));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getThumbnail()==null));
		assertTrue(hasEmbedField(resp, "Name","HelloSign"));
		assertTrue(hasEmbedField(resp, "Description","Client library for using the HelloSign API"));
		assertTrue(hasEmbedField(resp, "Namespace","`HelloSign`"));
		assertTrue(hasEmbedField(resp, embed->"Current Version".equals(embed.getName())));
		assertTrue(hasEmbedField(resp, "Authors","\"HelloSign\""));
		assertTrue(!hasEmbedField(resp, field->"Tags".equals(field.getName())));
		assertTrue(hasEmbedField(resp, "Verified","false"));
		assertTrue(hasEmbedField(resp, embed->"Downloads".equals(embed.getName())));
		resp.delete().queue();
	}
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new Nuget().getType());
	}
	@Test
	public void testHelp() {
		assertEquals("Get Nuget package info (mvn is better)", new Nuget().help());
	}

}
