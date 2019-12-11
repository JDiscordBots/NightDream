package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class FixedTest extends AbstractAdminCommandTest{
	@BeforeAll
	public static void init() {
		BotData.setFixedBugsChannel(getTestingChannel().getId());
		sendCommand("bugreport test bug: fix");
	}
	@Test
	public void testNoArgs() {
		sendCommand("fixed");
		Message resp=getMessage("Syntax: `fixed <id>|Original Bug[|<additional information>]`");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testNoComment() {
		sendCommand("fixed "+BotData.getBugID()+"|bug");
		Message resp=getMessage(msg->hasEmbed(msg, embed->
			("Fixed bug "+BotData.getBugID()).equals(embed.getTitle())&&
			hasEmbedField(embed, "Original bug","bug")&&
			("Reported as fixed by "+TestUtils.getJDA().getSelfUser().getName()).equals(embed.getFooter().getText())
		));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testTooHighBugID() {
		sendCommand("fixed "+BotData.getBugID()+1+"|some comment");
		Message resp=getMessage("This bug id is not valid!");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testWithComment() {
		sendCommand("fixed "+BotData.getBugID()+"|a bug|a comment");
		Message resp=getMessage(msg->hasEmbed(msg, embed->
			("Fixed bug "+BotData.getBugID()).equals(embed.getTitle())&&
			hasEmbedField(embed, "Original bug","a bug")&&
			hasEmbedField(embed, "Additional comment","a comment")&&
			("Reported as fixed by "+TestUtils.getJDA().getSelfUser().getName()).equals(embed.getFooter().getText())
		));
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testTooLowBugID() {
		sendCommand("fixed -1|some comment");
		Message resp=getMessage("This bug id is not valid!");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testInvalidArg() {
		sendCommand("fixed thisisinvalid");
		Message resp=getMessage("Syntax: `fixed <id>|Original Bug[|<additional information>]`");
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Reports a bug as fixed", new Fixed().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Fixed().getType());
	}
	@Override
	protected String cmdName() {
		return "fixed";
	}
	@Override
	protected Command cmd() {
		return new Fixed();
	}
}
