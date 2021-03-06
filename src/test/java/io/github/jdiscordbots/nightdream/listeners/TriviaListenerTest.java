/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: TriviaListenerTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.listeners;

import static io.github.jdiscordbots.jdatesting.TestUtils.getAlreadySentMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendMessage;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import net.dv8tion.jda.api.entities.Message;
@Execution(ExecutionMode.SAME_THREAD)
public class TriviaListenerTest {
	private void add(String answer) {
		TriviaListener.addQuestion(getTestingChannel(), answer);
		assertTrue(TriviaListener.getQuestions(getTestingChannel()).stream().anyMatch(q->q.contains(answer)));
	}
	@Test
	public void testAddQuestionWithAnswer() {
		add("test answer");
		sendMessage("test answer");
		Message resp=getMessage(getTestingChannel().getGuild().getSelfMember().getEffectiveName()+" got it!");
		assertNotNull(resp);
		assertNull(TriviaListener.getQuestions(getTestingChannel()));
		resp.delete().complete();//has to be deleted for other test
	}
	@Test
	public void testAddQuestionWithoutAnswer() {
		add("other answer");
		assertNull(getAlreadySentMessage(getTestingChannel(), msg->(getTestingChannel().getGuild().getSelfMember().getEffectiveName()+" got it!").equals(msg.getContentRaw())));
		assertNotNull(TriviaListener.getQuestions(getTestingChannel()));
		assertFalse(TriviaListener.getQuestions(getTestingChannel()).isEmpty());
		assertTrue(TriviaListener.getQuestions(getTestingChannel()).stream().filter(question->!question.isEmpty()).findAny().isPresent());
		Awaitility.await().pollDelay(30, TimeUnit.SECONDS).timeout(31, TimeUnit.SECONDS).ignoreException(ConditionTimeoutException.class).until(()->{
			Message resp=getMessage("Nobody got the answer this time. Sad.");
			if(resp==null) {
				return false;
			}else {
				resp.delete().queue();
				return true;
			}
		});
	}
}
