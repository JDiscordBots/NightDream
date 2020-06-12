/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: MsgLogTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.GuildAction;

public class MsgLogTest {
	/*@Test
	public void testAllowExecute() {
		Member selfMember=getTestingChannel().getGuild().getSelfMember();
		//need to remove perms somehow
		String[] admins=BotData.getAdminIDs();
		BotData.setAdminIDs(new String[0]);
		sendCommand("msglog");
		Message resp=getMessage("This command requires the Manage Messages permission.");
		BotData.setAdminIDs(admins);
		assertNotNull(resp);
		resp.delete().queue();
	}*/
	@Test
	public void testWithoutArguments() {
		sendCommand("msglog");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" I need a mentioned channel"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testClear() {
		TextChannel chan=getTestingChannel();
		BotData.setMsgLogChannel(chan.getId(), chan.getGuild());
		sendCommand("msglog none");
		Message resp=getMessage("Removed");
		assertNotNull(resp);
		resp.delete().queue();
		assertEquals("", BotData.getMsgLogChannel(chan.getGuild()));
	}
	@Test
	public void testInvalidArgs() {
		sendCommand("msglog test");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" I need a mentioned channel"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testValidExecution() {
		TextChannel chan=getTestingChannel();
		BotData.resetMsgLogChannel(chan.getGuild());
		sendCommand("msglog "+chan.getAsMention());
		Message resp=getMessage("Set! `" + BotData.getPrefix(chan.getGuild()) + "msglog none` to disable.");
		assertNotNull(resp);
		assertEquals(chan.getId(), BotData.getMsgLogChannel(chan.getGuild()));
		BotData.resetMsgLogChannel(chan.getGuild());
		resp.delete().queue();
	}
	@Test
	public void testPermNeeded() {
		assertEquals("Manage Messages", new MsgLog().permNeeded());
	}
	@Test
	public void testCrossMsgLogDenied() {
		Guild otherGuild = getOtherGuild();
		sendCommand("msglog "+otherGuild.getTextChannels().get(0).getAsMention());
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" You can only set msglog channels in the same server."));
		assertNotNull(resp);
		assertNotEquals(otherGuild.getTextChannels().get(0).getAsMention(),BotData.getMsgLogChannel(getTestingChannel().getGuild()));
		resp.delete().queue();
	}
	private Guild getOtherGuild() {
		return getJDA().getGuilds().stream().filter(g->!g.equals(getTestingChannel().getGuild())&&!g.getTextChannels().isEmpty()).findAny().orElseGet(()->{
			GuildAction createGuild = getJDA().createGuild("secondary guild for CI tests");
			createGuild.addChannel(createGuild.newChannel(ChannelType.TEXT, "testing_channel"));
			createGuild.complete();
			return getOtherGuild();
		});
	}
	@Test
	public void testHelp() {
		assertEquals("Sets up a log channel for deleted messages", new MsgLog().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.CONFIG, new MsgLog().getType());
	}
}
