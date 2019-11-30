package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class DnsTest {
	@Test
	public void testWithoutArgs() {
		sendCommand("dns");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" Unknown Resolve Target"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testInvalidQuery() {
		sendCommand("dns HelloWorld");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith("Your query is unresolvable.\nTry with a different one?"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testCorrectQuery() {
		sendCommand("dns discordapp.com");
		Message msg=getMessage(message->hasEmbed(message, embed->"discordapp.com (type 12) resolves to:".equals(embed.getTitle())));
		assertNotNull(msg);
		MessageEmbed embed=msg.getEmbeds().get(0);
		assertTrue(hasEmbedField(embed, "Type","6"));
		assertTrue(hasEmbedField(embed, field->"Time to live (TTL)".equals(field.getName())));
		assertTrue(hasEmbedField(embed, "Nameserver","gabe.ns.cloudflare.com"));
		assertTrue(hasEmbedField(embed, "DNS Hostmaster","dns.cloudflare.com"));
		assertTrue(hasEmbedField(embed, field->"Mystery Text".equals(field.getName())));
		//not testing footer because it may not exist
		msg.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("Resolve an address", new Dns().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new Dns().getType());
	}
}
