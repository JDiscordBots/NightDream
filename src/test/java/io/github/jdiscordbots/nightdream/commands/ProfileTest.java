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
			
			sendCommand("profile");
			resp=getMessage(msg->hasEmbed(msg, "test name","example Desc"));
			assertNotNull(resp);
			assertTrue(hasEmbedField(resp, "Banned from bug reports", "This User cannot send bug reports"));
			assertTrue(hasEmbedField(resp, "Links",
					"[GitHub](https://github.com/JDiscordBots/NightDream)\n"
					+ "[Daydream](https://gitlab.com/botstudio/daydream)"));
			assertTrue(hasEmbedField(resp, IconChooser.getQuestionIcon(getTestingChannel())+" Bot Admin!","This is a bot admin."));
			assertTrue(hasEmbed(resp, embed->0x123456==embed.getColorRaw()));
		}finally {
			BotData.STORAGE.remove("profile", "color", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("profile", "description", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("profile", "links", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("profile", "name", getJDA().getSelfUser().getId());
			BotData.STORAGE.remove("bugs", getJDA().getSelfUser().getId());
		}
		
	}
	//TODO not enough args, etc
	//test help
	@Test
	public void testHelp() {
		assertEquals("Shows & manages your profile, `profile help` for more", new Profile().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.FUN, new Profile().getType());
	}
}
