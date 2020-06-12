/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: AvatarTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.JDALoader;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class AvatarTest {
	@Test
	public void testWithoutArgs() {
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->hasEmbed(msg, embed->embed.getImage()!=null&&Objects.equals(embed.getImage().getUrl(),getJDA().getSelfUser().getAvatarUrl()))));
		sendCommand("avatar");
		Message msg=getMessage("You don't have an avatar...");
		assertNotNull(msg);
		msg.delete().queue();
	}
	@Test
	public void testWithMention() {
		Member testUser = JDALoader.getTestUser();
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->hasEmbed(msg, embed->embed.getImage()!=null&&Objects.equals(embed.getImage().getUrl(),getJDA().getSelfUser().getAvatarUrl()))));
		sendCommand("avatar "+testUser.getAsMention());
		Message resp=getMessage(msg->hasEmbed(msg, embed->embed.getImage()!=null&&embed.getImage().getUrl().equals(testUser.getUser().getAvatarUrl()+"?size=2048")));
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new Avatar().getType());
	}
	
	@Test
	public void testHelp() {
		assertEquals("Shows your (or someone else's) Avatar", new Avatar().help());
	}
}
