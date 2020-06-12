/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: PullTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.JDALoader;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PullTest {
	private void joinVoiceChannel() {
		VoiceChannel chan = getTestingChannel().getGuild().getVoiceChannels().stream().filter(vc->getTestingChannel().getGuild().getSelfMember().hasPermission(vc, Permission.VOICE_CONNECT)).findAny().orElse(null);
		getTestingChannel().getGuild().getAudioManager().openAudioConnection(chan);
		Awaitility.await().until(()->getTestingChannel().getGuild().getSelfMember().getVoiceState().inVoiceChannel());
	}
	private void leaveVoiceChannel() {
		getTestingChannel().getGuild().getAudioManager().closeAudioConnection();
		Awaitility.await().until(()->!getTestingChannel().getGuild().getSelfMember().getVoiceState().inVoiceChannel());
	}
	@Test
	public void testOutOfVoiceChannel() {
		sendCommand("pull");
		Message resp=getMessage("You're not in a voice channel");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testWithoutMention() {
		try{
			joinVoiceChannel();
			sendCommand("pull");
			Message resp=getMessage(IconChooser.getQuestionIcon(getTestingChannel())+" I need users do to that.");
			assertNotNull(resp);
			resp.delete().queue();
		}finally {
			leaveVoiceChannel();
		}
	}
	@Test
	public void testUserToPullOutOfVoiceChannel() {
		try {
			joinVoiceChannel();
			sendCommand("pull "+JDALoader.getTestUser().getAsMention());
			Message resp=getMessage("Please wait...\n"+JDALoader.getTestUser().getUser().getAsTag()+" is not in a voice channel - skipping!");
			assertNotNull(resp);
			resp.delete().queue();
		}finally {
			leaveVoiceChannel();
		}
	}
	//other user in voice channel-->not possible because the other user cannot be moved
	@Test
	public void testHelp() {
		assertEquals("Pull users into your voice channel", new Pull().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new Pull().getType());
	}
}
