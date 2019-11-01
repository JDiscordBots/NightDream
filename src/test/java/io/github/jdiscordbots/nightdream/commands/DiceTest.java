package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;

public class DiceTest {
	@Test
	public void testWithoutArgs() {
		sendCommand("dice");
		getMessage(msg->msg.getContentRaw().endsWith(" Not enough arguments!")).delete().queue();
	}
	@Test
	public void testWithNonNumericArg() {
		sendCommand("dice Hello");
		getMessage(msg->msg.getContentRaw().endsWith(" argument needs to be an integer!")).delete().queue();
	}
	private void testStandardExecution(int end) {
		sendCommand("dice "+end);
		getMessage(msg->hasEmbed(msg, "Rolling the dice...","From 1 to "+end));
		Awaitility.await().atMost(Durations.TEN_SECONDS).untilAsserted(()->getMessage(msg->hasEmbed(msg, embed->{
			int i;
			return embed.getTitle().equals("Done!")&&
			embed.getDescription().startsWith("It landed on a ")&&
			(i=Integer.parseInt(embed.getDescription().substring(15)))>=Math.min(1, end)&&
			i<=Math.max(1, end);
		})));
	}
	@Test
	public void testNegativeArg() {
		testStandardExecution(-5);
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
