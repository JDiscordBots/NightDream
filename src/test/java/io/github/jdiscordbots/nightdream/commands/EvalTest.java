package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;

public class EvalTest {
	@Test 
	public void testHelp() {
		assertEquals("Evaluates Code", new Eval().help());
	}
	@Test
	public void testPermissionString() {
		assertEquals("Bot-Admin", new Eval().permNeeded());
	}
	public void testCommandType() {
		assertSame(CommandType.META, new Eval().getType());
	}
	@Test
	public void testNonAdmin() {
		String[] adminIDs=BotData.getAdminIDs();
		BotData.setAdminIDs(Stream.of(adminIDs).filter(id->!id.equals(getJDA().getSelfUser().getId())).toArray(String[]::new));
		sendCommand("eval");
		getMessage(msg->msg.getContentRaw().endsWith(" This is an admin command.")).delete().complete();
		BotData.setAdminIDs(adminIDs);
	}
	@Test
	public void testNoErrorOnEmptyEval() {
		sendCommand("eval");
		assertThrows(ConditionTimeoutException.class, ()->getMessage(msg->msg.getContentRaw().startsWith("`ERROR`\n")));
	}
	@Test
	public void testSimpleExpression() {
		sendCommand("eval 1+1");
		getMessage("```java\n2\n```").delete().complete();
	}
	@Test
	public void testCorrectMessageObj() {
		sendCommand("eval message");
		getMessage("```java\n"+getMessage(msg->msg.getContentRaw().endsWith("eval message"))+"\n```").delete().complete();
	}
}
