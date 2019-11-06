package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.util.BotData;

public class ShellTest {
	@Test 
	public void testHelp() {
		assertEquals("execute a shell command", new Shell().help());
	}
	@Test
	public void testPermissionString() {
		assertEquals("Bot-Admin", new Shell().permNeeded());
	}
	public void testCommandType() {
		assertSame(CommandType.META, new Shell().getType());
	}
	@Test
	public void testNonAdmin() {
		String[] adminIDs=BotData.getAdminIDs();
		BotData.setAdminIDs(Stream.of(adminIDs).filter(id->!id.equals(getJDA().getSelfUser().getId())).toArray(String[]::new));
		sendCommand("shell");
		getMessage(msg->msg.getContentRaw().endsWith(" This is an admin command.")).delete().complete();
		BotData.setAdminIDs(adminIDs);
	}
	@Test
	public void testNoErrorOnEmptyEval() {
		sendCommand("shell");
		getMessage(msg->msg.getContentRaw().startsWith("Please specify a shell command.")).delete().queue();
	}
	@Test
	public void testSimpleExpression() {
		String cmd=System.getProperty("os.name").toLowerCase().contains("win")?"cmd /C echo test":"echo test";
		sendCommand("shell "+cmd);
		getMessage(msg->hasEmbed(msg, null,"**Command**: ```bash\n"+cmd+"```\n\n"+"**Output**(`stdout`): ```bash\ntest```")).delete().complete();
	}
	@Test
	public void testInvalidCommand() {
		sendCommand("shell invalidcommand");
		getMessage(msg->hasEmbed(msg, null,"**Command**: ```bash\ninvalidcommand```\n\nCannot start Process under "+System.getProperty("os.name"))).delete().complete();
	}
}
