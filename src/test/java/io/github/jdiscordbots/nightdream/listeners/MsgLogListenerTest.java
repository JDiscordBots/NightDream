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

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MsgLogListenerTest {
	private static MsgLogListener listener;
	private Message toDelete;
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
		try {
		getJDA().removeEventListener(listener);
		toDelete = getTestingChannel().sendMessage("test message 1").complete();
		getJDA().addEventListener(new ListenerAdapter() {
			@Override
			public void onMessageReceived(MessageReceivedEvent event) {
				if("test message 1".equals(event.getMessage().getContentRaw())){
					getJDA().addEventListener(listener);
					getJDA().removeEventListener(this);
				}
			}
		});
		Awaitility.await().atMost(Durations.FIVE_SECONDS).until(()->toDelete!=null);
		toDelete.delete().complete();
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->hasEmbed(msg, "Deleted Message",null)&&hasEmbedField(msg, "Message","test message 1")));
		}finally {
			getJDA().removeEventListener(listener);
			getJDA().addEventListener(listener);
		}
	}
	@Test
	public void testDeletedMsg() {
		Message toDelete=getTestingChannel().sendMessage("test message 2")
		.addFile(new byte[10], "empty.txt")
		.complete();
		toDelete.delete().queue();
		Message resp=getMessage(msg->hasEmbed(msg, "Deleted Message",null)&&hasEmbedField(msg, "Message","test message 2"));
		assertNotNull(resp);
		String avatarURL=getJDA().getSelfUser().getAvatarUrl();
		assertTrue(hasEmbed(resp, embed->avatarURL==null?embed.getThumbnail()==null:avatarURL.equals(embed.getThumbnail().getUrl())));
		assertTrue(hasEmbedField(resp, "Attachments","[empty.txt]("+toDelete.getAttachments().get(0).getUrl()+") (10)"));
		resp.delete().complete();
	}
	@AfterAll
	public static void finish() {
		BotData.resetMsgLogChannel(getTestingChannel().getGuild());
	}
}
