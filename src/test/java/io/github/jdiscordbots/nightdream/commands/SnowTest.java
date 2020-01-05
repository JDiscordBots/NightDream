package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class SnowTest {
	
	private static int increment=0;

	@Test
	public void testWithoutArguments() {
		sendCommand("snow");
		Message resp=getMessage(msg->msg.getContentRaw().matches("`[0-9]{18}` made `.+`"));
		assertNotNull(resp);
		String[] split=resp.getContentRaw().split("` made `");
		String id=split[0].substring(1);
		String sentTimestamp=split[1].substring(0, split[1].length()-1);
		long idLong=Long.parseLong(id);
		long timeSinceDCEpoch=idLong>>22;
		long timeSinceUnixEpoch=timeSinceDCEpoch+1_420_070_400_000L;
		String timestampCalculated=Instant.ofEpochMilli(timeSinceUnixEpoch).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzz)"));
		assertEquals(timestampCalculated, sentTimestamp);
		long worker=(idLong>>17)&0x1F;
		assertEquals(1, worker);
		long process=(idLong>>12)&0x1F;
		assertEquals(0, process);
		int increment=(int)idLong&0xFFF;
		assertEquals(SnowTest.increment++,increment);
		resp.delete().queue();
	}
	@Test
	public void testInvalidSnowflake() {
		sendCommand("snow a");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" Please provide a valid discord Snowflake."));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testSnowFlakeAnalysis() {
		//use example id 175928847299117063 from https://discordapp.com/developers/docs/reference
		sendCommand("snow 175928847299117063");
		Message resp=getMessage(msg->hasEmbed(msg, "175928847299117063", null));
		assertTrue(hasEmbedField(resp, "Binary","0000001001110001000001100101101011000001000000100000000000000111"));
		assertTrue(hasEmbedField(resp, "Date","2016-04-30"));
		assertTrue(hasEmbedField(resp, "Increment","7"));
		assertTrue(hasEmbedField(resp, "Worker, Process ID","175928847299117063 has worker ID 1 with process ID 0"));
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("Discord ID deconstructor/generator",new Snow().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.FUN,new Snow().getType());
	}
}
