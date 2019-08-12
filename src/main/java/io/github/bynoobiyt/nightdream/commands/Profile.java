package io.github.bynoobiyt.nightdream.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("profile")
public class Profile implements Command {
	private static Properties props;
	
	static {
		props=BotData.loadProperties("Profiles.properties", new HashMap<>(), "Profile data");
	}
	
	@Override
	public void action(String[] args, MessageReceivedEvent event) {//TODO test
		if(!event.getMessage().getMentionedUsers().isEmpty()) {
			User user = event.getMessage().getMentionedUsers().get(0);
			showProfile(event.getTextChannel(),user);
			return;
		}
		if(args.length<1) {
			showProfile(event.getTextChannel(), event.getAuthor());
			return;
		}
		EmbedBuilder builder=new EmbedBuilder();
		switch(args[0]) {
		case "description":
		case "desc":
			if(args.length<2) {
				event.getTextChannel().sendMessage("<:IconProvide:553870022125027329> I need more than 1 argument.").complete();
				return;
			}
			
			String desc=String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			builder.setDescription(desc).setTitle("Your description is now").setColor(0x212121);
			setProp(event.getAuthor(), "description", desc);
			break;
		case "color":
			if(args.length<2||args[1].length()!=7) {
				event.getTextChannel().sendMessage("Format <:IconThis:553869005820002324> `" + BotData.getPrefix(event.getGuild()) + "profile color #123456`").complete();
				return;
			}
			builder.setTitle("Set color!");
			String color=args[1].substring(1);
			builder.setColor(Integer.valueOf(color,16));
			setProp(event.getAuthor(), "color", color);
			break;
		case "help":
			builder.setColor(0x212121).setTitle("Profile Help");
			builder.addField(new Field("color", "Sets a profile color in #123456 format",false));
			builder.addField(new Field("description/desc", "Sets a profile description",false));
			builder.addField(new Field("name", "Sets your name",false));
			break;
		case "name":
			if(args.length<2) {
				event.getTextChannel().sendMessage("Format <:IconThis:553869005820002324> `" + BotData.getPrefix(event.getGuild()) + "profile name [new name]`").complete();//TODO link???
				return;
			}
			String name=String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			setProp(event.getAuthor(), "name", name);
			builder.setDescription("It is now "+name+".");
			break;
		default:
			showProfile(event.getTextChannel(), event.getAuthor());
			return;
		}
		JDAUtils.msg(event.getTextChannel(), builder.build(),false);
	}
	private void showProfile(TextChannel tc,User user) {
		EmbedBuilder builder=new EmbedBuilder();
		int color=0x212121;
		try {
			color=Integer.valueOf(getProp(user, "color"),16);
		}catch(NumberFormatException e) {
			//ignore
		}
		builder.setColor(color);
		builder.setTitle(getProp(user, "name", user.getAsTag()));
		builder.setDescription(getProp(user, "description", "A Ghost... yet"));
		if(JDAUtils.isOwner(user)) {
			builder.addField(new Field("<:IconInfo:553868326581829643> Bot Admin!", "This is a bot admin.", false));
		}
		JDAUtils.msg(tc, builder.build(),false);
	}
	private static String getProp(User user,String name) {
		return getProp(user,name,"");
	}
	private static String getProp(User user,String name,String defaultProp) {
		return props.getProperty(user.getId()+"."+name,defaultProp);
	}
	private static void setProp(User user,String name,String value) {
		props.setProperty(user.getId()+"."+name, value);
		BotData.saveProperties("Profiles.properties", props, "Profile data");
	}
	@Override
	public String help() {
		return "Shows & manages your profile, `profile help` for more";
	}

}
