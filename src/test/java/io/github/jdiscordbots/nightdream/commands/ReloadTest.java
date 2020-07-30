/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: ReloadTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

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
import java.util.Map;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import io.github.jdiscordbots.nightdream.listeners.MsgLogListener;
import io.github.jdiscordbots.nightdream.storage.Storage;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.JDAImpl;
import sun.misc.Unsafe;
@SuppressWarnings("restriction")
public class ReloadTest extends AbstractAdminCommandTest{
	private static ReloadStorage storage;
	private static Object staticFieldBase;
	private static long staticFieldOffset;
	private static Unsafe unsafe;
	@BeforeAll
	public static void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		getJDA();
		Storage oldStorage=BotData.STORAGE;
		
		Field storageField = BotData.class.getDeclaredField("STORAGE");
		
		final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        unsafe = (Unsafe) unsafeField.get(null);
		
        staticFieldBase = unsafe.staticFieldBase(storageField);
        staticFieldOffset = unsafe.staticFieldOffset(storageField);
        storage=new ReloadStorage(oldStorage);
        unsafe.putObject(staticFieldBase, staticFieldOffset, storage);
	}
	@AfterAll
	public static void finish() throws IllegalArgumentException, IllegalAccessException {
		unsafe.putObject(staticFieldBase, staticFieldOffset, storage.getForward());
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
	public void testGReconnect() {
		int msgsToChack=TestUtils.getNumOfMessagesToCheck();
		try {
			TestUtils.setNumOfMessagesToCheck(5);
			sendCommand("reload login");
			Awaitility.await().atMost(Durations.TEN_SECONDS).until(()->!((JDAImpl)getJDA()).getClient().isConnected());
			Awaitility.await().atMost(Durations.TEN_SECONDS).until(()->getJDA().getStatus()==Status.CONNECTED);
			Message resp=getMessage(msg->hasEmbed(msg, null,"reconnecting..."));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->Color.YELLOW.equals(embed.getColor())));
			resp.delete().queue();
			resp=getMessage(msg->hasEmbed(msg, null,"reconnected!"));
			assertNotNull(resp);
			assertTrue(hasEmbed(resp, embed->Color.GREEN.equals(embed.getColor())));
			resp.delete().queue();
			assertEquals(0, storage.getFullReloadCount());
			assertTrue(storage.getGuildReloads().isEmpty());
		}finally {
			TestUtils.setNumOfMessagesToCheck(msgsToChack);
		}
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
