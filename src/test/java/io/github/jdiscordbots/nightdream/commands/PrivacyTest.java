package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class PrivacyTest {

	@Test
	public void testExecution() {
		//requires correct privacy-policy.md, may not work with custom policies
		sendCommand("privacy");
		Message resp=getMessage(msg->hasEmbed(msg, "Privacy Policy", null));
		assertNotNull(resp);
		MessageEmbed embed=resp.getEmbeds().get(0);
		assertTrue(embed.getFields().size()>1);
		boolean first=true;
		for (Field field :embed .getFields()) {
			if(first) {
				assertEquals(EmbedBuilder.ZERO_WIDTH_SPACE,field.getName());
				first=false;
			}else {
				assertTrue(field.getName().length()>1);
			}
		}
		assertNotNull(embed.getFooter());
		resp.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Displays the privacy policy",new Privacy().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Privacy().getType());
	}
}
