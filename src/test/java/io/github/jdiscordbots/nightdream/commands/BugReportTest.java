package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class BugReportTest {
	@BeforeAll
	public static void init() {
		BotData.setBugReportChannel(getTestingChannel().getId());
	}
	@Test
	public void testEmptyBugReport() {
		sendCommand("bugreport");
		Message resp=getMessage("Please provide a message for the bugreport.");
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testCorrectBugReport() {
		int id=BotData.getBugID()+1;
		sendCommand("bugreport test bug");
		Message resp=getMessage(getTestingChannel(),
				msg->hasEmbed(msg, embed->
			"New Bug".equals(embed.getTitle())&&
			"test bug".equals(embed.getDescription())&&
			(getJDA().getSelfUser().getAsTag() + " | Bug ID " + id).equals(embed.getFooter().getText())
		));
		assertNotNull(resp);
		resp.delete().queue();
		resp=getMessage("Sent with ID "+id);
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Files a bug report", new BugReport().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META,new BugReport().getType());
	}
}
