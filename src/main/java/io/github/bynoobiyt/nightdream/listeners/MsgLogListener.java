package io.github.bynoobiyt.nightdream.listeners;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@BotListener
public class MsgLogListener extends ListenerAdapter {

	private Map<String, Message> messages=new HashMap<>();
	
	public void clearCache() {
		messages.clear();
	}
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		if(!BotData.getMsgLogChannel(event.getGuild()).equals("")) {
			Message msg=messages.get(event.getMessageId());
			if(msg==null) {
				Logger.getGlobal().log(Level.INFO,"A message that has not been cached was deleted.");
			}else {
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0x212121)
				.setTitle("Deleted Message")
				.setFooter(msg.getAuthor()+" in channel "+msg.getChannel().getName())
				.setTimestamp(Instant.now());
				String text="nothing";
				if(msg.getType()==MessageType.DEFAULT){
					text=msg.getContentDisplay().substring(0,Math.min(1024,msg.getContentDisplay().length()));
				}
				builder.addField("Message", text, false);
				if(msg.getAuthor().getAvatarUrl()!=null) {
					builder.setThumbnail(msg.getAuthor().getAvatarUrl().split("\\	?size")[0]);
				}
				if(!msg.getAttachments().isEmpty()) {
					StringBuilder attachmentsBuilder=new StringBuilder("\n\n");
					for (Attachment attachment : msg.getAttachments()) {
						attachmentsBuilder.append("[")
						.append(attachment.getFileName())
						.append("]")
						.append("(")
						.append(attachment.getUrl())
						.append(") (")
						.append(attachment.getSize());
					}
					builder.addField("Attachments", attachmentsBuilder.toString(), false);
				}
				event.getGuild().getTextChannelById(BotData.getMsgLogChannel(event.getGuild())).sendMessage(builder.build()).queue();
			}
			
		}
		
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(!BotData.getMsgLogChannel(event.getGuild()).equals("")) {
			messages.put(event.getMessageId(), event.getMessage());
		}
	}
}
