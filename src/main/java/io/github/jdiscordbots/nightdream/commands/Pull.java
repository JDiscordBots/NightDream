/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Pull.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.IconChooser;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("pull")
public class Pull implements Command {

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS)||JDAUtils.checkOwner(event,args!=null);
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(!event.getMember().getVoiceState().inVoiceChannel()) {
			event.getChannel().sendMessage("You're not in a voice channel").queue();
			return;
		}
		if(event.getMessage().getMentionedMembers().isEmpty()) {
			event.getChannel().sendMessage(IconChooser.getQuestionIcon(event.getChannel())+" I need users do to that.").queue();
			return;
		}
		event.getChannel().sendMessage("Please wait...").queue(msg->{
			for(Member member : event.getMessage().getMentionedMembers()) {
				if(member.getVoiceState().inVoiceChannel()) {
					event.getGuild().moveVoiceMember(member, event.getMember().getVoiceState().getChannel()).queue(x->
						msg.editMessage(msg.getContentRaw()+"\n"+member.getUser().getAsTag()+" was moved successfully").queue()
					);
				}else {
					msg.editMessage(msg.getContentRaw()+"\n"+member.getUser().getAsTag()+" is not in a voice channel - skipping!").queue();
				}
			}
		});
	}

	@Override
	public String help() {
		return "Pull users into your voice channel";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}

}
