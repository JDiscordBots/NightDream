package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTimeout;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static io.github.jdiscordbots.jdatesting.TestUtils.setTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Duration;

import org.awaitility.Durations;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class DiceTest {
	@Test
	public void testWithoutArgs() {
		sendCommand("dice");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" Not enough arguments!"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testWithNonNumericArg() {
		sendCommand("dice Hello");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" argument needs to be an integer!"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	private void testStandardExecution(int end) {
		testStandardExecution(end,Math.min(1, end),Math.max(1, end));
	}
	private void testStandardExecution(int end,int from,int to) {
		sendCommand("dice "+end);
		Duration defaultTimeout=getTimeout();
		setTimeout(Durations.TEN_SECONDS);
		Message resp=getMessage(msg->hasEmbed(msg, embed->{
			int i;
			return "Done!".equals(embed.getTitle())&&
			embed.getDescription()!=null&&
			embed.getDescription().startsWith("It landed on a ")&&
			(i=Integer.parseInt(embed.getDescription().substring(15)))>=from&&
			i<=to;
		}));
		assertNotNull(resp);
		resp.delete().queue();
		setTimeout(defaultTimeout);
	}
	@Test
	public void testNegativeArg() {
		testStandardExecution(-5);
	}
	@Test
	public void testZero() {
		testStandardExecution(0,0,0);
	}
	@Test
	public void testNormalArg() {
		testStandardExecution(10);
	}
	@Test
	public void testHelp() {
		assertEquals("Rolls a random number from one", new Dice().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL,new Dice().getType());
	}
}
