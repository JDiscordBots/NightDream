/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: CalcTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import static io.github.jdiscordbots.jdatesting.TestUtils.*;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class CalcTest{
	private String formatEnd=String.format("Format: `%scalc num1 [+,-,*,/,%%,**,root] num2`", 
			BotData.getPrefix(getTestingChannel().getGuild()));
	private Message getWrongFormatMsg() {
		return getMessage(msg->msg.getContentRaw().endsWith(formatEnd));
	}
	private void assertWrongFormat(String command) {
		sendCommand(command);
		Message message =getWrongFormatMsg();
		assertNotNull(message);
		message.delete().complete();
	}
	private void secureNoMessage(Predicate<Message> condition) {
		assertNull(getAlreadySentMessage(getTestingChannel(), condition));
	}
	private void secureNoWrongFormat() {
		secureNoMessage(msg->msg.getContentRaw().endsWith(formatEnd));
	}
	private void assertCalculation(String calculation,String title,String result) {
		sendCommand("math "+calculation);
		Message message =getMessage(msg->hasEmbed(msg, title,result));
		assertNotNull(message);
		message.delete().complete();
	}
	@Test
	public void testWrongFormat() {
		secureNoWrongFormat();
		assertWrongFormat("math");
		assertWrongFormat("math 1 +");
		assertWrongFormat("math 1 x 2");
		assertWrongFormat("math a + 2");
	}
	@Test
	public void testAdd() {
		assertCalculation("1 + 1", "1.0 plus 1.0", "2.0");
		assertCalculation("-100 + -1", "-100.0 plus -1.0", "-101.0");
		assertCalculation("-Infinity + Infinity", "-Infinity plus Infinity", "NaN");
		assertCalculation("NaN + 1", "NaN plus 1.0", "NaN");
	}
	@Test
	public void testSubtract() {
		assertCalculation("1 - 1", "1.0 minus 1.0", "0.0");
		assertCalculation("-100 - -1", "-100.0 minus -1.0", "-99.0");
		assertCalculation("-100 - 1", "-100.0 minus 1.0", "-101.0");
		assertCalculation("Infinity - Infinity", "Infinity minus Infinity", "NaN");
		assertCalculation("NaN - 1", "NaN minus 1.0", "NaN");
	}
	@Test
	public void testMultiply() {
		assertCalculation("1 * 1", "1.0 by 1.0", "1.0");
		assertCalculation("-100 * -1", "-100.0 by -1.0", "100.0");
		assertCalculation("-100 * 1", "-100.0 by 1.0", "-100.0");
		assertCalculation("-Infinity * Infinity", "-Infinity by Infinity", "-Infinity");
		assertCalculation("NaN * 1", "NaN by 1.0", "NaN");
	}
	@Test
	public void testDivide() {
		assertCalculation("1 / 1", "1.0 divided by 1.0", "1.0");
		assertCalculation("-1000 / -1", "-1000.0 divided by -1.0", "1000.0");
		assertCalculation("-100 / -1", "-100.0 divided by -1.0", "100.0");
		assertCalculation("1 / 0", "1.0 divided by 0.0", "Infinity");
		assertCalculation("-Infinity / 0", "-Infinity divided by 0.0", "-Infinity");
		assertCalculation("0 / 0", "0.0 divided by 0.0", "NaN");
		assertCalculation("-Infinity / Infinity", "-Infinity divided by Infinity", "NaN");
		assertCalculation("NaN / 1", "NaN divided by 1.0", "NaN");
	}
	@Test
	public void testPower() {
		assertCalculation("1 ** 1", "1.0 exponented by 1.0", "1.0");
		assertCalculation("-100 ** 99", "-100.0 exponented by 99.0", "-1.0E198");
		assertCalculation("-100 ** -1", "-100.0 exponented by -1.0", "-0.01");
		assertCalculation("100 ** 0", "100.0 exponented by 0.0", "1.0");
		assertCalculation("-Infinity ** 0", "-Infinity exponented by 0.0", "1.0");
		assertCalculation("0 ** 0", "0.0 exponented by 0.0", "1.0");
		assertCalculation("-Infinity ** Infinity", "-Infinity exponented by Infinity", "Infinity");
		assertCalculation("NaN ** 1", "NaN exponented by 1.0", "NaN");
	}
	@Test
	public void testRoot() {
		assertCalculation("1 root 1", "1.0 root of 1.0", "1.0");
		assertCalculation("-2 root 4", "-2.0 root of 4.0", "0.5");
		assertCalculation("-100 root -1", "-100.0 root of -1.0", "NaN");
		assertCalculation("100 root 0", "100.0 root of 0.0", "0.0");
		assertCalculation("-Infinity root 0", "-Infinity root of 0.0", "1.0");
		assertCalculation("0 root 0", "0.0 root of 0.0", "0.0");
		assertCalculation("-Infinity root Infinity", "-Infinity root of Infinity", "1.0");
		assertCalculation("NaN root 1", "NaN root of 1.0", "NaN");
	}
	@Test
	public void testModulu() {
		assertCalculation("1 % 1", "1.0 mod 1.0", "0.0");
		assertCalculation("-6 % 4", "-6.0 mod 4.0", "-2.0");
		assertCalculation("-100 % -1", "-100.0 mod -1.0", "-0.0");
		assertCalculation("100 % 0", "100.0 mod 0.0", "NaN");
		assertCalculation("-Infinity % 0", "-Infinity mod 0.0", "NaN");
		assertCalculation("0 % 0", "0.0 mod 0.0", "NaN");
		assertCalculation("-Infinity % Infinity", "-Infinity mod Infinity", "NaN");
		assertCalculation("NaN % 1", "NaN mod 1.0", "NaN");
	}
	@Test
	public void testDecimals() {
		assertCalculation("4.5 + 1.5", "4.5 plus 1.5", "6.0");
		assertCalculation("4.5 + 1.0", "4.5 plus 1.0", "5.5");
	}
	
	@Test
	public void testHelp() {
		assertEquals("Does some calculation for you", new Calc().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL,new Calc().getType());
	}
}
