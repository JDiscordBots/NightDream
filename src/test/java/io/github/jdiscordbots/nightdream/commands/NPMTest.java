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

public class NPMTest {
	
	@Test
	public void testWithoutArgs() {
		sendCommand("npm");
		Message resp=getMessage(msg->msg.getContentRaw().endsWith(" I need a package name"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testInvalidPackage() {
		sendCommand("npm thisisinvalid");
		Message resp=getMessage("Are you sure the package exists?");
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testValidPackage() {
		sendCommand("npm daydream-logging");
		Message resp=getMessage(msg->hasEmbed(msg, "Result",null));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "name","`daydream-logging`"));
		assertTrue(hasEmbedField(resp, "Description","console.log wrapper to imitate colorful logging"));
		assertTrue(hasEmbedField(resp, embed->"Current Version".equals(embed.getName())));
		assertTrue(hasEmbedField(resp, "Keywords","`\"logging\", \"console\", \"log\", \"colors\"`"));
		assertTrue(hasEmbedField(resp, "Author","NPM says `sp46` | package.json says Infi"));
		resp.delete().queue();
	}
	
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new NPM().getType());
	}
	@Test
	public void testHelp() {
		assertEquals("Allows you to view info about a npm package", new NPM().help());
	}

}
