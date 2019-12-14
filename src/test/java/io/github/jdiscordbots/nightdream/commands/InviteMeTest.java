package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class InviteMeTest {

	@Test
	public void inviteMeTest() {
		sendCommand("inviteme");
		Message resp=getMessage(msg->hasEmbed(msg, "Invites","[Add the bot](https://discordapp.com/api/oauth2/authorize?client_id="+getJDA().getSelfUser().getId()+"&permissions=8&scope=bot)"+System.lineSeparator()+"[Server invite](https://discordapp.com/invite/KjMsK5G)"));
		assertNotNull(resp);
		resp.delete().queue();
	}
	
	@Test
	public void testHelp() {
		assertEquals("Invites the bot", new InviteMe().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.META, new InviteMe().getType());
	}
}
