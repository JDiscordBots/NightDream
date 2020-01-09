package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class PhotoTest {
	private static final String NO_KEY_DISABLE="\"\".equals(Java.type(\"io.github.jdiscordbots.nightdream.util.BotData\").getPixaBayAPIKey())";
	
	@BeforeAll
	public static void setUp() {
		getTestingChannel();//load jda-testing-system and BotData before BotData is loaded by the disable condition
	}
	
	@Test
	public void testWithoutArguments() {
		sendCommand("photo");
		Message resp = getMessage(msg->msg.getContentRaw().endsWith(" Search Query, please."));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testWithoutAPIKey() {
		String apiKey=BotData.getPixaBayAPIKey();
		try {
			BotData.setPixaBayAPIKey("");
			sendCommand("photo test");
			Message resp=getMessage(msg->hasEmbed(msg, null,"This command is disabled because there is no API Key set."));
			assertNotNull(resp);
			resp.delete().queue();
		}finally {
			BotData.setPixaBayAPIKey(apiKey);
		}
	}
	@DisabledIf(NO_KEY_DISABLE)
	@Test
	public void testNormalExecution() {
		sendCommand("photo test");
		Message resp=getMessage(msg->hasEmbed(msg, "Result",null));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->"Results from Pixabay [https://pixabay.com]".equals(embed.getFooter().getText())));
		assertTrue(hasEmbed(resp, embed->embed.getImage().getUrl().startsWith("https://pixabay.com/get/")));
		resp.delete().queue();
	}
	@DisabledIf(NO_KEY_DISABLE)
	@Test
	public void testInvalidImage() {
		sendCommand("photo thisisaninvalidargument");
		Message resp=getMessage(msg->hasEmbed(msg, embed->embed.getTitle().endsWith(" Nothing found")&&"Try something different.".equals(embed.getDescription())));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("Gets a photo from Pixabay", new Photo().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.IMAGE, new Photo().getType());
	}
}
