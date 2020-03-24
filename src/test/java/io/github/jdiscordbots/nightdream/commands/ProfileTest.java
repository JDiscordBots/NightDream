package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.IconChooser;
import net.dv8tion.jda.api.entities.Message;

public class ProfileTest {

	@Test
	public void testDefaultProfile() {
		sendCommand("profile");
		Message resp=getMessage(msg->hasEmbed(msg, getJDA().getSelfUser().getAsTag(),"A Ghost... yet"));
		assertNotNull(resp);
		assertFalse(hasEmbedField(resp, "Banned from bug reports", "This User cannot send bug reports"));
		assertFalse(hasEmbedField(resp, field->"links".equals(field.getName())));
		assertTrue(hasEmbedField(resp, IconChooser.getQuestionIcon(getTestingChannel())+" Bot Admin!","This is a bot admin."));
		assertTrue(hasEmbed(resp, embed->0x212121==embed.getColorRaw()));
		resp.delete().queue();
	}
	@Test
	public void testProfileUpdates() {
		String[] originalAdmins=BotData.getAdminIDs();
		try {
			sendCommand("profile desc example Desc");
			Message resp=getMessage(msg->hasEmbed(msg, "Your description is now","example Desc"));
			assertNotNull(resp);
			resp.delete().queue();
			
			sendCommand("profile color #123456");
			resp=getMessage(msg->hasEmbed(msg, "Set color!",null));
			assertTrue(hasEmbed(resp, embed->0x123456==embed.getColorRaw()));
			assertNotNull(resp);
			resp.delete().queue();
			
			sendCommand("profile name test name");
			resp=getMessage(msg->hasEmbed(msg,null, "It is now test name."));
			assertNotNull(resp);
			resp.delete().queue();
			
			sendCommand("profile link GitHub https://github.com/JDiscordBots/NightDream");
			resp=getMessage(msg->hasEmbed(msg, "Link added!",null));
			assertNotNull(resp);
			assertTrue(hasEmbedField(resp,"`GitHub`","https://github.com/JDiscordBots/NightDream"));
			resp.delete().queue();
			
			sendCommand("profile link Daydream https://gitlab.com/botstudio/daydream");
			resp=getMessage(msg->hasEmbed(msg, "Link added!",null));
			assertNotNull(resp);
			assertTrue(hasEmbedField(resp,"`Daydream`","https://gitlab.com/botstudio/daydream"));
			resp.delete().queue();
			
			BotData.STORAGE.write("bugs", getJDA().getSelfUser().getId(), "banned");
			BotData.setAdminIDs(new String[0]);
			
			sendCommand("profile");
			resp=getMessage(msg->hasEmbed(msg, "test name","example Desc"));
			assertNotNull(resp);
			assertTrue(hasEmbedField(resp, "Banned from bug reports", "This User cannot send bug reports"));
			assertTrue(hasEmbedField(resp, "Links",
					"[GitHub](https://github.com/JDiscordBots/NightDream)\n"
					+ "[Daydream](https://gitlab.com/botstudio/daydream)"));
			assertFalse(hasEmbedField(resp, IconChooser.getQuestionIcon(getTestingChannel())+" Bot Admin!","This is a bot admin."));
			assertTrue(hasEmbed(resp, embed->0x123456==embed.getColorRaw()));
			resp.delete().queue();
			
		}finally {
			BotData.STORAGE.remove("profile", "color", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("profile", "description", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("profile", "links", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("profile", "name", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("bugs", getJDA().getSelfUser().getId());
			BotData.setAdminIDs(originalAdmins);
		}
	}
	@Test
	public void testLinkReset() {
		BotData.STORAGE.write("profile","links", getJDA().getSelfUser().getId(), "GitHub|https://github.com/JDiscordBots/NightDream");
		sendCommand("profile link reset");
		Message resp=getMessage(msg->hasEmbed(msg, "resetted links",null));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getColorRaw()==0x212121));
		assertEquals("", BotData.STORAGE.read("profile",  "links", getJDA().getSelfUser().getId(),""));
		resp.delete().queue();
	}
	@Test
	public void testInvalidArgs() {
		sendCommand("profile description");
		Message resp=getMessage(IconChooser.getQuestionIcon(getTestingChannel())+" I need more than 1 argument.");
		assertNotNull(resp);
		resp.delete().queue();
		
		sendCommand("profile color");
		resp=getMessage("Format "+IconChooser.getArrowIcon(getTestingChannel())+" `"+BotData.getPrefix(getTestingChannel().getGuild())+"profile color #123456`");
		assertNotNull(resp);
		resp.delete().queue();
		
		sendCommand("profile color #12345");
		resp=getMessage("Format "+IconChooser.getArrowIcon(getTestingChannel())+" `"+BotData.getPrefix(getTestingChannel().getGuild())+"profile color #123456`");
		assertNotNull(resp);
		resp.delete().queue();
		
		sendCommand("profile color #123456 a");
		resp=getMessage("Format "+IconChooser.getArrowIcon(getTestingChannel())+" `"+BotData.getPrefix(getTestingChannel().getGuild())+"profile color #123456`");
		assertNotNull(resp);
		resp.delete().queue();
		
		sendCommand("profile name");
		resp=getMessage("Format "+IconChooser.getArrowIcon(getTestingChannel())+" `"+BotData.getPrefix(getTestingChannel().getGuild())+"profile name [new name]`");
		assertNotNull(resp);
		resp.delete().queue();
		
		sendCommand("profile link");
		resp=getMessage(IconChooser.getQuestionIcon(getTestingChannel())+" I need more than 1 argument.");
		assertNotNull(resp);
		resp.delete().queue();
		
		sendCommand("profile link something");
		resp=getMessage(IconChooser.getQuestionIcon(getTestingChannel())+" I need more than 2 arguments.");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testSubHelp() {
		sendCommand("profile help");
		Message resp=getMessage(msg->hasEmbed(msg, "Profile Help",null));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->embed.getColorRaw()==0x212121));
		assertTrue(hasEmbedField(resp, "color", "Sets a profile color in #123456 format"));
		assertTrue(hasEmbedField(resp, "description/desc", "Sets a profile description"));
		assertTrue(hasEmbedField(resp, "name", "Sets your name"));
		assertTrue(hasEmbedField(resp, "link", "adds a link to your profile or resets all links (`link reset`)"));
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("Shows & manages your profile, `profile help` for more", new Profile().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.FUN, new Profile().getType());
	}
}
