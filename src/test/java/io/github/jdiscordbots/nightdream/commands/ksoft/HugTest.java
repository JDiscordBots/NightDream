/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: HugTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands.ksoft;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.JDALoader;
import net.dv8tion.jda.api.entities.Message;

public class HugTest extends AbstractKSoftImageTest{
	@Override
	protected String getName() {
		return "hug";
	}
	@Test
	public void testHelp() {
		assertEquals("Hugs someone or yourself :)", new Hug().help());
	}
	@Test
	public void testWithMention() {
		sendCommand(getName()+" "+JDALoader.getTestUser().getAsMention());
		Message resp;
		if(doesTokenExists()) {
			resp=getMessage(msg->hasEmbed(msg, "**"+JDALoader.getTestUser().getEffectiveName()+"** has been hugged by **"+getTestingChannel().getGuild().getSelfMember().getEffectiveName()+"**!",null));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->embed.getImage()!=null));
		}else {
			resp=getMessage(msg->hasEmbed(msg, null,"This command is disabled due there is no KSoft API token"));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->Color.RED.equals(embed.getColor())));
		}
		resp.delete().queue();
	}
	@Override
	protected String getInfo() {
		return "**"+getTestingChannel().getGuild().getSelfMember().getEffectiveName()+"** has been hugged by **"+getTestingChannel().getGuild().getSelfMember().getEffectiveName()+"**!";
	}
}
