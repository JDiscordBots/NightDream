package io.github.jdiscordbots.nightdream.listeners;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;

public class MsgLogListenerTest {
	private static MsgLogListener listener;
	@BeforeAll
	public static void setUp() {
		BotData.setMsgLogChannel(getTestingChannel().getId(), getTestingChannel().getGuild());
		for (Object obj : getJDA().getRegisteredListeners()) {
			if(obj instanceof MsgLogListener) {
				listener=((MsgLogListener)obj);
			}
		}
		assertNotNull(listener);
		BotData.setMsgLogChannel(getTestingChannel().getId(), getTestingChannel().getGuild());
	}
	
	@Test
	public void testNotCachedMsg() {
		Message toDelete = getTestingChannel().sendMessage("test message 1").complete();
		listener.clearCache();
		toDelete.delete().complete();
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->hasEmbed(msg, "Deleted Message 1",null)));
	}
	@Test
	public void testDeletedMsg() {
		Message toDelete=getTestingChannel().sendMessage("test message 2")
		.addFile(new byte[10], "empty.txt")
		.complete();
		toDelete.delete().queue();
		Message resp=getMessage(msg->hasEmbed(msg, "Deleted Message",null));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "Message","test message 2"));
		String avatarURL=getJDA().getSelfUser().getAvatarUrl();
		assertTrue(hasEmbed(resp, embed->avatarURL==null?embed.getThumbnail()==null:avatarURL.equals(embed.getThumbnail().getUrl())));
		assertTrue(hasEmbedField(resp, "Attachments","[empty.txt]("+toDelete.getAttachments().get(0).getUrl()+") (10)"));
	}
	@AfterAll
	public static void finish() {
		BotData.resetMsgLogChannel(getTestingChannel().getGuild());
	}
}
