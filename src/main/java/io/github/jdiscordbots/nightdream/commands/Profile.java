/*
 * Copyright (c) JDiscordBots 2019
 * File: Profile.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BotCommand("profile")
public class Profile implements Command {
	private static final String COLOR_PROP_NAME="color";
	private static final String DESC_PROP_NAME="description";
	private static final String LINK_PROP_NAME="links";
	
	private static final String DESC_CMD_1="description";
	private static final String DESC_CMD_2="desc";
	private static final String COLOR_CMD="color";
	private static final String HELP_CMD="help";
	private static final String NAME_CMD="name";
	private static final String LINK_CMD="link";
	
	private static final String STORAGE_UNIT="profile";
	
	private static final Pattern LINK_REGEX=Pattern.compile("https?://([A-Za-z0-9+-].*)?([.].+)|/.*");
	private static final Pattern DIFFERENT_LINKS_SPLITTER=Pattern.compile("\\|\\|");
	private static final Pattern LINK_NAME_SPLITTER=Pattern.compile("\\|");
	
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(!event.getMessage().getMentionedUsers().isEmpty()) {
			User user = event.getMessage().getMentionedUsers().get(0);
			showProfile(event.getChannel(),user);
			return;
		}
		if(args.length<1) {
			showProfile(event.getChannel(), event.getAuthor());
			return;
		}
		EmbedBuilder builder=new EmbedBuilder();
		switch(args[0]) {
		case DESC_CMD_1:
		case DESC_CMD_2:
			desc(builder,args,1,event);
			break;
		case COLOR_CMD:
			color(builder,args,1,event);
			break;
		case HELP_CMD:
			help(builder);
			break;
		case NAME_CMD:
			name(builder,args,1,event);
			break;
		case LINK_CMD:
			link(builder,args,1,event);
			break;
		default:
			showProfile(event.getChannel(), event.getAuthor());
			return;
		}
		if(!builder.isEmpty()) {
			JDAUtils.msg(event.getChannel(), builder.build());
		}
	}
	private static void showProfile(TextChannel tc,User user) {
		EmbedBuilder builder=new EmbedBuilder();
		int color=0x212121;
		try {
			color=Integer.valueOf(getProp(user, COLOR_PROP_NAME),16);
		}catch(NumberFormatException e) {
			//ignore
		}
		
		builder.setColor(color);
		builder.setTitle(getProp(user, "name", user.getAsTag()));
		builder.setDescription(getProp(user, DESC_PROP_NAME, "A Ghost... yet"));
		if (BotData.STORAGE.read("bugs", user.getId(), "").equals("banned")) {
			builder.addField("Banned from bug reports", "This User cannot send bug reports", false);
		}
		String links=getProp(user,LINK_PROP_NAME);
		if(!"".equals(links)) {
			builder.addField("Links",
					Stream.of(DIFFERENT_LINKS_SPLITTER.split(links))
					.map(link->"["+LINK_NAME_SPLITTER.split(link)[0]+"]("+LINK_NAME_SPLITTER.split(link)[1]+")")
					.collect(Collectors.joining("\n")),false);
		}
		if(JDAUtils.isOwner(user)) {
			builder.addField(new Field("<:IconInfo:553868326581829643> Bot Admin!", "This is a bot admin.", false));
		}
		JDAUtils.msg(tc, builder.build());
	}
	private static String getProp(User user,String name) {
		return getProp(user,name,"");
	}
	private static String getProp(User user,String name,String defaultProp) {
		return BotData.STORAGE.read(STORAGE_UNIT, name, user.getId(), defaultProp, COLOR_PROP_NAME,DESC_PROP_NAME,LINK_PROP_NAME,"name");
	}
	private static void setProp(User user,String name,String value) {
		BotData.STORAGE.write(STORAGE_UNIT, name, user.getId(), value, COLOR_PROP_NAME,DESC_PROP_NAME,LINK_PROP_NAME,"name");
	}
	private static void unsetProp(User user,String name) {
		BotData.STORAGE.remove(STORAGE_UNIT, name, user.getId());
	}
	private static void desc(EmbedBuilder builder,String[] args,int offset,GuildMessageReceivedEvent event) {
		if(args.length<offset+1) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need more than "+offset+" argument"+(offset==1?"":"s")+".").queue();
			return;
		}
		
		String desc=String.join(" ", Arrays.copyOfRange(args, offset, args.length));
		builder.setDescription(desc).setTitle("Your description is now").setColor(0x212121);
		setProp(event.getAuthor(), DESC_PROP_NAME, desc);
	}
	private static void color(EmbedBuilder builder,String[] args,int offset,GuildMessageReceivedEvent event) {
		if(args.length<offset+1||args[offset].length()!=7) {
			event.getChannel().sendMessage("Format <:IconThis:553869005820002324> `" + BotData.getPrefix(event.getGuild()) + "profile color #123456`").queue();
			return;
		}
		builder.setTitle("Set color!");
		String color=args[offset].substring(1);
		builder.setColor(Integer.valueOf(color,16));
		setProp(event.getAuthor(), COLOR_PROP_NAME, color);
	}
	private static void name(EmbedBuilder builder,String[] args,int offset,GuildMessageReceivedEvent event) {
		if(args.length<offset+1) {
			event.getChannel().sendMessage("Format <:IconThis:553869005820002324> `" + BotData.getPrefix(event.getGuild()) + "profile name [new name]`").queue();
			return;
		}
		String name=String.join(" ", Arrays.copyOfRange(args, offset, args.length));
		setProp(event.getAuthor(), "name", name);
		builder.setDescription("It is now "+name+".");
	}
	private static void link(EmbedBuilder builder,String[] args,int offset,GuildMessageReceivedEvent event) {
		if(args.length==offset+1&&"reset".equalsIgnoreCase(args[offset])) {
			unsetProp(event.getAuthor(), LINK_PROP_NAME);
			builder.setTitle("resetted links")
			.setColor(0x212121);
			return;
		}
		if(args.length<offset+2) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need more than "+offset+" argument"+(offset==0?"":"s")+".").queue();
			return;
		}
		String name=args[offset];
		String link=String.join("+",Arrays.copyOfRange(args, offset+1, args.length));
		
		
		link=link.replace("[", "%5B").replace("]", "%5D").replace("(", "%28").replace(")", "%29").replace("|", "%7C");
		name=name.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(").replace(")", "\\)");
		
		if(!LINK_REGEX.matcher(link).matches()) {
			event.getChannel().sendMessage("<:IconX:553868311960748044> Sorry, this is not a link.").queue();
			return;
		}
		
		builder.setTitle("Link added!")
		.setColor(0x212121)
		.addField("`"+name+"`", link, false);
		
		String links=getProp(event.getAuthor(), LINK_PROP_NAME);
		link=name+"|"+link;
		if("".equals(links)) {
			links=link;
		}else {
			links+="||"+link;
		}
		setProp(event.getAuthor(), LINK_PROP_NAME, links);
	}
	private static void help(EmbedBuilder builder) {
		builder.setColor(0x212121).setTitle("Profile Help");
		builder.addField(new Field(COLOR_CMD, "Sets a profile color in #123456 format",false));
		builder.addField(new Field(DESC_CMD_1+"/"+DESC_CMD_2, "Sets a profile description",false));
		builder.addField(new Field(NAME_CMD, "Sets your name",false));
		builder.addField(new Field(LINK_CMD, "adds a link to your profile or resets all links (`"+LINK_CMD+" reset`)", false));
	}
	
	@Override
	public String help() {
		return "Shows & manages your profile, `profile help` for more";
	}

	@Override
	public CommandType getType() {
		return CommandType.FUN;
	}
}
