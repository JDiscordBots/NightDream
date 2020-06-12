/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: EvalTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

public class EvalTest extends AbstractAdminCommandTest{
	
	private static final Logger LOG=LoggerFactory.getLogger(EvalTest.class);
	
	@Test 
	public void testHelp() {
		assertEquals("Evaluates Code", new Eval().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Eval().getType());
	}
	private void testNull(String code) {
		sendCommand("eval "+code);
		Message resp=getMessage(msg->msg.getContentRaw().startsWith("`ERROR`\n"));
		assertNull(resp);
		resp=getMessage(msg->hasEmbed(msg, embed->embed.getFooter()!=null&&embed.getFooter().getText().startsWith("null | ")));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testNoErrorOnEmptyExpression() {
		testNull("");
	}
	@Test
	public void testInlineComment() {
		testNull("//");
	}
	@Test
	public void testMultiLineComment() {
		testNull("/*");
	}
	@Test
	public void testSimpleExpression() {
		sendCommand("eval return Integer.valueOf(1+1);");
		Message resp=getMessage(msg->hasEmbed(msg,null, "```java\n2\n```"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getFooter().getText().startsWith("java.lang.Integer | ")));
		resp.delete().queue();
	}
	@Test
	public void testCorrectMessageObj() {
		sendCommand("eval return message;");
		Message cmdMsg=getMessage(message->message.getContentRaw().endsWith("eval return message;"));
		assertNotNull(cmdMsg);
		Message resp=getMessage(msg->hasEmbed(msg,null, "```java\n"+cmdMsg+"\n```"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getFooter().getText().startsWith(ReceivedMessage.class.getCanonicalName()+" | ")));
		resp.delete().queue();
	}
	@Test
	public void testPrimitiveReturnType() {
		sendCommand("eval return 0;");
		Message resp;
		if(ToolProvider.getSystemJavaCompiler()==null) {
			LOG.info("testing eval without a java compiler");
			resp = getMessage("`ERROR`\n```java\nInvalid return type - The method must either return an object or nothing.\n```");
		}else {
			LOG.info("testing eval with a java compiler");
			resp=getMessage(msg->hasEmbed(msg,null, "```java\n0\n```"));
		}
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testJavaStreams() {
		if(ToolProvider.getSystemJavaCompiler()!=null) {
			sendCommand("eval return Stream.of(3,4,5).filter(number->number%2==0).findFirst().orElse(-1);");
			Message resp=getMessage(msg->hasEmbed(msg,null, "```java\n4\n```"));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->embed.getFooter().getText().startsWith("java.lang.Integer | ")));
			resp.delete().queue();
		}
	}
	@Override
	protected String cmdName() {
		return "eval";
	}
	@Override
	protected Command cmd() {
		return new Eval();
	}
}
