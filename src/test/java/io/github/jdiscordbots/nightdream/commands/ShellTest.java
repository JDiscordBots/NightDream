package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;

public class ShellTest extends AbstractAdminCommandTest{
	
	private static final String OS_NAME=System.getProperty("os.name");
	
	@Test 
	public void testHelp() {
		assertEquals("execute a shell command ("+OS_NAME+")", new Shell().help());
	}
	
	public void testCommandType() {
		assertSame(CommandType.META, new Shell().getType());
	}
	@Test
	public void testNoErrorOnEmptyEval() {
		sendCommand("shell");
		getMessage(msg->msg.getContentRaw().startsWith("Please specify a shell command.")).delete().queue();
	}
	@Test
	public void testSimpleExpression() {
		String cmd=OS_NAME.toLowerCase().contains("win")?"cmd /C echo test":"echo test";
		sendCommand("shell "+cmd);
		getMessage(msg->hasEmbed(msg, null,"**Command**: ```bash\n"+cmd+"```\n\n"+"**Output**(`stdout`): ```bash\ntest\n```")).delete().complete();
	}
	@Test
	public void testInvalidCommand() {
		sendCommand("shell invalidcommand");
		getMessage(msg->hasEmbed(msg, embed->embed.getDescription().startsWith("**Command**: ```bash\ninvalidcommand```\n\nCannot start Process under "+OS_NAME))).delete().complete();
	}

	@Override
	protected String cmdName() {
		return "shell";
	}

	@Override
	protected Command cmd() {
		return new Shell();
	}
}