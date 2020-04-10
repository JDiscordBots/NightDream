package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getJDA;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.listeners.MsgLogListener;
import io.github.jdiscordbots.nightdream.storage.Storage;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.JDAImpl;

@EnabledOnJre(value = JRE.JAVA_8)//Field seems not to have the attribute modifiers on newer JDK versions
public class ReloadTest extends AbstractAdminCommandTest{
	
	private static Field storageField;
	private static ReloadStorage storage;
	@BeforeAll
	public static void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		getJDA();
		Storage oldStorage=BotData.STORAGE;
		storageField = BotData.class.getDeclaredField("STORAGE");
		storageField.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
	    modifiersField.setAccessible(true);
	    modifiersField.setInt(storageField, storageField.getModifiers() & ~Modifier.FINAL);
	    storage=new ReloadStorage(oldStorage);
	    storageField.set(null, storage);
	}
	@AfterAll
	public static void finish() throws IllegalArgumentException, IllegalAccessException {
		storageField.set(null, storage.getForward());
	}
	@AfterEach
	public void resetStats() {
		storage.resetReloadStats();
	}
	private void testFullPropertiesReload() {
		Message resp=getMessage(msg->hasEmbed(msg, null,"reloading all Properties..."));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.YELLOW.equals(embed.getColor())));
		resp.delete().queue();
		resp=getMessage(msg->hasEmbed(msg, null,"reloaded!"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.GREEN.equals(embed.getColor())));
		resp.delete().queue();
		assertEquals(1, storage.getFullReloadCount());
		assertTrue(storage.getGuildReloads().isEmpty());
	}
	@Test
	public void testWithoutArgs() {
		sendCommand("reload");
		testFullPropertiesReload();
	}
	@Test
	@Disabled("This test is temporarily disabled as it crashes most tests executed after it.")
	public void testReconnect() {
		sendCommand("reload login");
		Awaitility.await().atMost(Durations.ONE_SECOND).until(()->!((JDAImpl)getJDA()).getClient().isConnected());
		Message resp=getMessage(msg->hasEmbed(msg, null,"reconnecting..."));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.YELLOW.equals(embed.getColor())));
		resp.delete().queue();
		Awaitility.await().atMost(Durations.TEN_SECONDS).until(()->getJDA().getStatus()==Status.CONNECTED);
		resp=getMessage(msg->hasEmbed(msg, null,"reconnected!"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.GREEN.equals(embed.getColor())));
		resp.delete().queue();
		assertEquals(0, storage.getFullReloadCount());
		assertTrue(storage.getGuildReloads().isEmpty());
		
	}
	@Test
	public void testPropsReload() {
		sendCommand("reload props");
		testFullPropertiesReload();
	}
	@Test
	public void testGuildPropsReload() {
		sendCommand("reload guild");
		Message resp=getMessage(msg->hasEmbed(msg, null,"reloading guild Properties..."));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.YELLOW.equals(embed.getColor())));
		resp.delete().queue();
		resp=getMessage(msg->hasEmbed(msg, null,"reloaded guild Properties!"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.GREEN.equals(embed.getColor())));
		resp.delete().queue();
		assertEquals(0, storage.getFullReloadCount());
		assertEquals(1,storage.getGuildReloads().size());
	}
	@Test
	public void testMsgCacheReload() {
		sendCommand("reload msgCache");
		Message resp=getMessage(msg->hasEmbed(msg, null,"message cache reloaded!"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.GREEN.equals(embed.getColor())));
		resp.delete().queue();
		int msgLogListenerCount=0;
		for (Object obj : getJDA().getRegisteredListeners()) {
			if(obj instanceof MsgLogListener) {
				MsgLogListener listener=(MsgLogListener)obj;
				Map<String, Message> messages = listener.getMessages();
				messages.remove(resp.getId());
				assertTrue(messages.isEmpty());
				msgLogListenerCount++;
			}
		}
		assertEquals(1, msgLogListenerCount);
	}
	@Test
	public void testInvalidArgument() {
		sendCommand("reload thisisinvalid");
		Message resp=getMessage(msg->hasEmbed(msg, null,"Invalid argument thisisinvalid"));
		assertNotNull(resp);
		assertTrue(hasEmbed(resp, embed->Color.RED.equals(embed.getColor())));
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("reconnects (`reload login`/`reload reconnect`),\nreloads settings for the current guild(`reload guild`) or everything(`reload props`)\nor deletes the message cache(`reload msgcache`)", new Reload().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.CONFIG, new Reload().getType());
	}
	@Override
	protected String cmdName() {
		return "reload";
	}
	@Override
	protected Command cmd() {
		return new Reload();
	}
}
