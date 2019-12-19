package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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
	public void testHelp() {
		assertEquals("Sets up a log channel for deleted messages", new MsgLog().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.CONFIG, new MsgLog().getType());
	}
}
