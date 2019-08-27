package io.github.bynoobiyt.nightdream.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bynoobiyt.nightdream.util.GeneralUtils;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("npm")
public class NPM implements Command{

	private static final Logger LOG=LoggerFactory.getLogger(NPM.class);
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a package name").queue();
		}
		String url="http://registry.yarnpkg.com/"+args[0];
		try(Scanner scan=new Scanner(new URL(url).openConnection().getInputStream(), StandardCharsets.UTF_8.name())){
			String json=scan.nextLine();
			String scope;
			if(args[0].startsWith("@")) {
				scope=args[0].substring(1).split("/")[0];
			}else {
				scope="undefined";
			}
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0xfb3b49)
			.setTitle("Result")
			.addField(new Field("name", "`"+GeneralUtils.getJSONString(json, "name")+"`", true))
			.addField(new Field("Description", GeneralUtils.getJSONString(json, "description"), true))
			.addField(new Field("Current Version", GeneralUtils.getJSONString(json, "latest"), true))
			.addField(new Field("Keywords", "`"+GeneralUtils.getMultipleJSONStrings(json, "keywords")+"`", true))
			.addField(new Field("Author", GeneralUtils.getJSONString(json, "author\":{\"name"), true))
			.addField(new Field("Scope", "`"+scope+"`", true));
			
			JDAUtils.msg(event.getChannel(), builder.build());
		}catch(FileNotFoundException e) {
			JDAUtils.errmsg(event.getChannel(), "Not found");
		}catch (IOException e) {
			JDAUtils.errmsg(event.getChannel(), "An error occured.");
			LOG.warn("IO Error while executing an npm query",e);
		}
	}
	@Override
	public String help() {
		return "Allows you to view info about an NPM package";
	}
	
}
