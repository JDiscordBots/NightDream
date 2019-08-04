package io.github.bynoobiyt.nightdream.util;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Utils {

	public static final long INFO_TIMEOUT=5000;
	
	
	private Utils() {
		//prevent instantiation
	}
	
	/**
	 * sends an Error Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 */
	public static void errmsg(TextChannel channel, String text) {
		msg(channel, text, Color.RED, true);
	}
	/**
	 * send a Message
	 * standardColor: {@link Color#GREEN}
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, String text) {
		return msg(channel, text, Color.GREEN, false);
	}
	/**
	 * send a Message
	 * standardColor: {@link Color#GREEN}
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @param timeout should the Message be deleted automatically
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, String text,boolean timeout) {
		return msg(channel, text, Color.GREEN, timeout);
	}
	
	/**
	 * send a Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param text The text of the Message as {@link String}
	 * @param color the {@link Color} of the Message
	 * @param timeout should the Message be deleted automatically
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, String text,Color color,boolean timeout) {
		return msg(channel, new EmbedBuilder()
					.setColor(color)
					.setDescription(text)
					.build(), timeout);
	}
	/**
	 * send a Message
	 * @param channel The {@link TextChannel} where the Message should be sent
	 * @param message The content of the Message as {@link MessageEmbed}
	 * @param timeout should the Message be deleted automatically
	 * @return the sent {@link Message}
	 */
	public static Message msg(TextChannel channel, MessageEmbed message,boolean timeout) {
		try {
			Message msg = channel.sendMessage(message).complete();
			if (timeout) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							msg.delete().queue();
						} catch (IllegalArgumentException e) {
							//continue with execution
						}
					}
				}, INFO_TIMEOUT);
			}
			return msg;
		} catch (Exception e) {
			System.err.println("Cannot send Message \""+message.getDescription()+"\" in channel "+channel.getName()+"["+channel.getGuild().getName()+"] because of a "+e.getClass().getSimpleName());
			return null;
		}
	}
	
	/**
	 * tests if the Author of a Message is the Developer of this Bot<br>
	 * if not an errormessage will be sent.
	 * @param event the {@link MessageReceivedEvent} of the Message
	 * @return <code>true</code> if the Author is the Developer, else <code>false</code>
	 */
	public static boolean checkOwner(MessageReceivedEvent event) {
		return checkOwner(event, true);
	}
	/**
	 * tests if the Author of a Message is the Developer of this Bot<br>
	 * if forbidden and doErrMsg is true an errormessage will be sent.
	 * @param event the {@link MessageReceivedEvent} of the Message
	 * @param doErrMsg should an Error-Message be sent?
	 * @return <code>true</code> if the Author is the Developer, else <code>false</code>
	 */
	public static boolean checkOwner(MessageReceivedEvent event,boolean doErrMsg) {
		if(event.getAuthor().getId().equals(BotData.getAdminID())) {
			return true;
		}
		if (doErrMsg) {
			errmsg(event.getTextChannel(),"You need to be Bot-Admin to do that!");
		}
		return false;
	}
	
}
