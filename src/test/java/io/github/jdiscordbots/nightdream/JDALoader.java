/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: JDALoader.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream;

import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.setNumOfMessagesForAutoDelete;
import static io.github.jdiscordbots.jdatesting.TestUtils.setNumOfMessagesToCheck;
import static io.github.jdiscordbots.jdatesting.TestUtils.setTimeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.awaitility.Durations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jdiscordbots.jdatesting.TestUtils;
import io.github.jdiscordbots.nightdream.core.NightDream;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class JDALoader {
	
	private static final Logger LOG=LoggerFactory.getLogger(JDALoader.class);
	private static final Logger TEST_LOG=LoggerFactory.getLogger(TestUtils.class);
	
	private static Member testUser;
	private JDALoader() {
		//prevent instantiation
	}
	public static JDA load() {
		System.setProperty("profile", "test");
		String env=System.getenv("ND_token");
		if(env!=null) {
			BotData.setToken(env);
		}
		if((env=System.getenv("ND_admin"))!=null) {
			BotData.setAdminIDs(env.split(" "));
		}
		BotData.setGame("automated Feature-Testing");
		JDA jda=NightDream.initialize().getShardById(0);
		String[] adminIDs = BotData.getAdminIDs();
		List<String> ids=new ArrayList<>(Arrays.asList(adminIDs));
		if(!ids.contains(jda.getSelfUser().getId())) {
			ids.add(jda.getSelfUser().getId());
			BotData.setAdminIDs(ids.stream().toArray(String[]::new));
		}
		LOG.info("Admins: "+Arrays.toString(BotData.getAdminIDs()));
		if(TEST_LOG.isInfoEnabled()) {
			TestUtils.setLogger(str->TEST_LOG.debug(str));
		}
		setTimeout(Durations.TEN_SECONDS);
		setNumOfMessagesToCheck(2);
		setNumOfMessagesForAutoDelete(5);
		return jda;
	}
	public static Member getTestUser() {
		if(testUser==null) {
			String env=System.getenv("ND_testuser");
			if(env!=null) {
				testUser=getTestingChannel().getGuild().retrieveMemberById(env).complete();
			}
			if(testUser==null) {
				testUser=getTestingChannel().getGuild().retrieveOwner().complete();
			}
		}
		return testUser;
	}
}
