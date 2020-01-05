package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.getTestingChannel;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.listeners.TriviaListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class TriviaTest {
	@Test
	public void testTriviaFlow() {
		sendCommand("trivia");
		Message resp=getMessage(msg->hasEmbed(msg, embed->"Trivia".equals(embed.getTitle())&&"Type your answer in this channel!".equals(embed.getFooter().getText())));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, field->"Choices:".equals(field.getName())));
		List<Field> fields=resp.getEmbeds().get(0).getFields();
		assertEquals(2, fields.size());
		assertTrue(TriviaListener.getQuestions(getTestingChannel()).stream().anyMatch(question->Stream.of(fields.stream().filter(field->field.getName().equals("Choices:")).findAny().get().getValue().toLowerCase().split(", ")).anyMatch(possibleAnswer->question.contains(possibleAnswer))));
		TriviaListener.getQuestions(getTestingChannel()).clear();
		resp.delete().queue();
	}
}
