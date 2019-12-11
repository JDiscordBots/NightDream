package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

public class EvalTest extends AbstractAdminCommandTest{
	@Test 
	public void testHelp() {
		assertEquals("Evaluates Code", new Eval().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Eval().getType());
	}
	
	@Test
	public void testNoErrorOnEmptyExpression() {
		sendCommand("eval");
		Message resp=getMessage(msg->msg.getContentRaw().startsWith("`ERROR`\n"));
		assertNull(resp);
	}
	@Test
	public void testSimpleExpression() {
		sendCommand("eval 1+1");
		Message resp=getMessage(msg->hasEmbed(msg,null, "```java\n2\n```"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getFooter().getText().startsWith("java.lang.Integer | ")));
		resp.delete().queue();
	}
	@Test
	public void testCorrectMessageObj() {
		sendCommand("eval message");
		Message resp=getMessage(msg->hasEmbed(msg,null, "```java\n"+getMessage(message->message.getContentRaw().endsWith("eval message"))+"\n```"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getFooter().getText().startsWith(ReceivedMessage.class.getCanonicalName()+" | ")));
		resp.delete().queue();
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
