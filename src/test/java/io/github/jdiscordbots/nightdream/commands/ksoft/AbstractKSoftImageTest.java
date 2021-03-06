/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: AbstractKSoftImageTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.core.CommandHandler;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public abstract class AbstractKSoftImageTest {
	private static final Logger LOG=LoggerFactory.getLogger(AbstractKSoftImageTest.class);
	private static boolean init = false;
	private static boolean tokenExists;

	@BeforeAll
	public static void init() {
		if (!init) {
			getJDA();
			if ("".equals(BotData.getKSoftToken()) || BotData.getKSoftToken() == null) {
				tokenExists=false;
				LOG.warn("There is no KSoft API token provided.");
			} else {
				tokenExists=true;
			}
			init = true;
		}
	}

	@Test
	public void testExecution() {
		sendCommand(getName());
		Message resp;
		if(tokenExists) {
			resp=getMessage(msg->hasEmbed(msg, getInfo(),null));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->embed.getImage()!=null));
		}else {
			resp=getMessage(msg->hasEmbed(msg, null,"This command is disabled due there is no KSoft API token"));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->Color.RED.equals(embed.getColor())));
		}
		resp.delete().queue();
	}

	@Test
	public void testCommandType() {
		assertSame(CommandType.IMAGE, CommandHandler.getCommands().get(getName()).getType());
	}

	protected abstract String getName();
	protected abstract String getInfo();
	
	protected static boolean doesTokenExists() {
		return tokenExists;
	}
}
