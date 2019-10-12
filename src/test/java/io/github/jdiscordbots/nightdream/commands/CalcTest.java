package io.github.jdiscordbots.nightdream.commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import static io.github.jdiscordbots.jdatesting.TestUtils.*;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class CalcTest{
	private String formatEnd=String.format("Format: `%scalc num1 [+,-,*,/,%%,**,root] num2`", 
			BotData.getPrefix(getTestingChannel().getGuild()));
	@Test
	public void testWrongFormat() {
		sendCommand("math");
		Message message = getMessage(getTestingChannel(), msg->msg.getContentRaw().endsWith(formatEnd));
		assertNotNull(message);
	}
}
