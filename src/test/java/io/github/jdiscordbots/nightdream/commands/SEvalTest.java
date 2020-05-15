package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class SEvalTest extends AbstractAdminCommandTest{
	
	@Test 
	public void testSuccessfulExecution() {
		sendCommand("seval channel.sendMessage(\"Hello\").queue();");
		Message resp=getMessage("Hello");
		assertNotNull(resp);
		resp.delete().queue();
		resp=getAlreadySentMessage(getTestingChannel(),msg->hasEmbed(msg, embed->embed.getFooter()!=null&&embed.getFooter().getText().startsWith("null | ")));
		assertNull(resp);
	}
	@Test 
	public void testError() {
		int msgsToChack=TestUtils.getNumOfMessagesToCheck();
		try {
			TestUtils.setNumOfMessagesToCheck(5);
			sendCommand("seval thisisinvalid");
			sendCommand("seval thisisinvalidtoo");
			Message resp=getMessage("No...");
			assertNotNull(resp);
			Message respDel=getMessage(msg->"No...".equals(msg.getContentRaw())&&!msg.getId().equals(resp.getId()));
			assertNotNull(respDel);
			assertNotEquals(resp.getId(),respDel.getId());
			resp.clearReactions().queue();
			Awaitility.await().atLeast(59, TimeUnit.SECONDS).atMost(65,TimeUnit.SECONDS).until(()->getAlreadySentMessage(getTestingChannel(),msg->msg.getId().equals(respDel.getId()))==null);
			Message stillResp=getAlreadySentMessage(getTestingChannel(), msg->msg.getId().equals(resp.getId()));
			assertNotNull(stillResp);
			stillResp.delete().queue();
		}finally {
			TestUtils.setNumOfMessagesToCheck(msgsToChack);
		}
	}
	@Test 
	public void testHelp() {
		assertEquals("Evaluates Code, but silently", new SEval().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new Eval().getType());
	}
	@Override
	protected String cmdName() {
		return "seval";
	}
	@Override
	protected Command cmd() {
		return new SEval();
	}
}
