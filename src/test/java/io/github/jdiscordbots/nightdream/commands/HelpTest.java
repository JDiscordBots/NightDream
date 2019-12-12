package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.core.NightDream;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class HelpTest {
	private Guild g=getTestingChannel().getGuild();

	@Test
	public void testWithoutArguments() {
		sendCommand("help");
		Message resp=getMessage(msg->hasEmbed(msg, embed->"Nightdream Commands".equals(embed.getTitle())));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->("Prefix in "+g.getName()+": "+BotData.getPrefix(g)+" | Release "+NightDream.VERSION).equals(embed.getFooter().getText())));
		assertTrue(hasEmbedField(resp, field->"Util".equals(field.getName())));
		assertTrue(hasEmbedField(resp, field->"Fun".equals(field.getName())));
		assertTrue(hasEmbedField(resp, field->"Config".equals(field.getName())));
		assertTrue(hasEmbedField(resp, field->"Meta".equals(field.getName())));
		assertTrue(hasEmbedField(resp, field->"Image".equals(field.getName())));
		for (MessageEmbed embed : resp.getEmbeds()) {
			for (Field field : embed.getFields()) {
				assertTrue(field.getValue().split("\n").length>4);
			}
			
		}
		resp.delete().queue();
	}
	@Test
	public void testWithCommand() {
		sendCommand("help help");
		Message resp=getMessage(msg->hasEmbed(msg, embed->"Help with help".equals(embed.getTitle())));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->("Prefix in "+g.getName()+": "+BotData.getPrefix(g)+" | Release "+NightDream.VERSION).equals(embed.getFooter().getText())));
		assertTrue(hasEmbedField(resp, "Category","Meta"));
		assertTrue(hasEmbedField(resp, "Permissions needed","<none>"));
		assertTrue(hasEmbedField(resp, "Description","¯\\_(ツ)_/¯"));
		resp.delete().queue();
	}
	@Test
	public void testWithAdminCommand() {
		sendCommand("help eval");
		Message resp=getMessage(msg->hasEmbed(msg, embed->"Help with eval".equals(embed.getTitle())));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->("Prefix in "+g.getName()+": "+BotData.getPrefix(g)+" | Release "+NightDream.VERSION).equals(embed.getFooter().getText())));
		assertTrue(hasEmbedField(resp, "Category","Meta"));
		assertTrue(hasEmbedField(resp, "Permissions needed","Bot-Admin"));
		assertTrue(hasEmbedField(resp, "Description","Evaluates Code"));
		resp.delete().queue();
	}
	@Test
	public void testWithInvalidCommand() {
		sendCommand("help helpp");
		Message resp=getMessage(msg->hasEmbed(msg, "Unknown Command","But I have `help`."));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("¯\\_(ツ)_/¯", new Help().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Help().getType());
	}
}
