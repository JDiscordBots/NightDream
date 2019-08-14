package io.github.bynoobiyt.nightdream.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("profile")
public class Profile implements Command {
	private static Properties props;
	
	static {
		reload();
	}
	public static void reload() {
		props=BotData.loadProperties("Profiles.properties", new HashMap<>(), "Profile data");
	}
	
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
		case "description":
		case "desc":
			if(args.length<2) {
				event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need more than 1 argument.").queue();
				return;
			}
			
			String desc=String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			builder.setDescription(desc).setTitle("Your description is now").setColor(0x212121);
			setProp(event.getAuthor(), "description", desc);
			break;
		case "color":
			if(args.length<2||args[1].length()!=7) {
				event.getChannel().sendMessage("Format <:IconThis:553869005820002324> `" + BotData.getPrefix(event.getGuild()) + "profile color #123456`").queue();
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
				event.getChannel().sendMessage("Format <:IconThis:553869005820002324> `" + BotData.getPrefix(event.getGuild()) + "profile name [new name]`").queue();
				return;
			}
			String name=String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			setProp(event.getAuthor(), "name", name);
			builder.setDescription("It is now "+name+".");
			break;
		case "link":
			if(args.length<3) {
				event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need more than 1 argument.").queue();
				return;
			}
			name=args[1];
			String link=String.join(" ",Arrays.copyOfRange(args, 2, args.length));
			if(name.contains("|")||link.contains("|")) {
				event.getChannel().sendMessage("<:IconX:553868311960748044> Sorry, Links cannot contain pipes. Use `%7C` instead.").queue();
				return;
			}
			if(!link.startsWith("http")) {
				event.getChannel().sendMessage("<:IconX:553868311960748044> Sorry, this is not a link. Make sure to include `http/s`.").queue();
				return;
			}
			
			builder.setTitle("Link added!")
			.setColor(0x212121)
			.addField("`"+name+"`", link, false);
			
			String links=getProp(event.getAuthor(), "links");
			link=name+"|"+link.replace("[", "%5B").replace("]", "%5D").replace("(", "%28").replace(")", "%29");
			if(links.equals("")) {
				links=link;
			}else {
				links+="||"+link;
			}
			setProp(event.getAuthor(), "links", links);
			break;
		default:
			showProfile(event.getChannel(), event.getAuthor());
			return;
		}
		JDAUtils.msg(event.getChannel(), builder.build());
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
		String links=getProp(user,"links");
		if(!links.equals("")) {
			builder.addField("Links",
					Stream.of(links.split("\\|\\|"))
					.map(link->"["+link.split("\\|")[0]+"]("+link.split("\\|")[1]+")")
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
