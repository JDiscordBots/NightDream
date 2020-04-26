/*
 * Copyright (c) JDiscordBots 2019
 * File: MsgLogListener.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.listeners;

import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.map.ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BotListener
public class MsgLogListener extends ListenerAdapter {

	private Map<String, Message> messages=new ReferenceMap<>();
	private static final Logger LOG=LoggerFactory.getLogger(MsgLogListener.class);
	private static final Pattern SIZE_SPLIT=Pattern.compile("\\	?size");
	
	public void clearCache() {
		messages.clear();
	}
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		if(!"".equals(BotData.getMsgLogChannel(event.getGuild()))) {
			Message msg=messages.get(event.getMessageId());
			if(msg==null) {
				LOG.info("A message that has not been cached was deleted.");
			}else {
				messages.remove(event.getMessageId());
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0x212121)
				.setTitle("Deleted Message")
				.setFooter(msg.getAuthor().getAsTag()+" in channel "+msg.getChannel().getName())
				.setTimestamp(Instant.now());
				String text="nothing";
				if(msg.getType()==MessageType.DEFAULT){
					text=msg.getContentDisplay().substring(0,Math.min(1024,msg.getContentDisplay().length()));
				}
				builder.addField("Message", text, false);
				if(msg.getAuthor().getAvatarUrl()!=null) {
					builder.setThumbnail(SIZE_SPLIT.split(msg.getAuthor().getAvatarUrl())[0]);
				}
				addAttachments(msg, builder);
				event.getGuild().getTextChannelById(BotData.getMsgLogChannel(event.getGuild())).sendMessage(builder.build()).queue();
			}
		}
	}
	
	private void addAttachments(Message msg,EmbedBuilder builder) {
		if(!msg.getAttachments().isEmpty()) {
			StringBuilder attachmentsBuilder=new StringBuilder("\n\n");
			for (Attachment attachment : msg.getAttachments()) {
				attachmentsBuilder.append("[")
				.append(attachment.getFileName())
				.append("]")
				.append("(")
				.append(attachment.getUrl())
				.append(") (")
				.append(attachment.getSize())
				.append(")\n");
			}
			builder.addField("Attachments", attachmentsBuilder.toString(), false);
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(!(BotData.getMsgLogChannel(event.getGuild())==null||BotData.getMsgLogChannel(event.getGuild()).isEmpty())) {
			messages.put(event.getMessageId(), event.getMessage());
		}
	}

	public Map<String, Message> getMessages() {
		return messages;
	}
}
