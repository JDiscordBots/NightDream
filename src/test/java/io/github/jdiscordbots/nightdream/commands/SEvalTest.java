/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: SEvalTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTimeout;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static io.github.jdiscordbots.jdatesting.TestUtils.setTimeout;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class SEvalTest extends AbstractAdminCommandTest{
	
	@Test 
	public void testSuccessfulExecution() {
		Duration prevTimeout = getTimeout();
		try {
			setTimeout(prevTimeout.multipliedBy(3));
			sendCommand("seval channel.sendMessage(\"Hello\").queue();");
			Message resp = getMessage("Hello");
			assertNotNull(resp);
			resp.delete().queue();
			resp = getAlreadySentMessage(getTestingChannel(), msg -> hasEmbed(msg,
					embed -> embed.getFooter() != null && embed.getFooter().getText().startsWith("null | ")));
			assertNull(resp);
		} finally {
			TestUtils.setTimeout(prevTimeout);
		}
	}
	@Test 
	public void testError() {
		int msgsToChack=TestUtils.getNumOfMessagesToCheck();
		try {
			TestUtils.setNumOfMessagesToCheck(5);
			long timeBefore=System.currentTimeMillis();
			sendCommand("seval thisisinvalid");
			sendCommand("seval thisisinvalidtoo");
			Message resp=getMessage("No...");
			assertNotNull(resp);
			Message respDel=getMessage(msg->"No...".equals(msg.getContentRaw())&&msg.getIdLong()!=resp.getIdLong());
			assertNotNull(respDel);
			assertNotEquals(resp.getId(),respDel.getId());
			resp.clearReactions().complete();
			long timeAfter=System.currentTimeMillis();
			int timeDiffSeconds=(int) Math.ceil((timeAfter-timeBefore)/(double)100);
			Awaitility.await().atLeast(60-timeDiffSeconds, TimeUnit.SECONDS).atMost(65,TimeUnit.SECONDS).until(()->getAlreadySentMessage(getTestingChannel(),msg->msg.getId().equals(respDel.getId()))==null);
			Message stillResp=getAlreadySentMessage(getTestingChannel(), msg->msg.getIdLong()==resp.getIdLong());
			assertNotNull(stillResp);
			stillResp.delete().queue();
		}finally {
			TestUtils.setNumOfMessagesToCheck(msgsToChack);
		}
	}
	@Test 
	public void testHelp() {
		assertEquals("Evaluates Code, but silently", new SEval().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Eval().getType());
	}
	@Override
	protected String cmdName() {
		return "seval";
	}
	@Override
	protected Command cmd() {
		return new SEval();
	}
}
