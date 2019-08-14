package io.github.bynoobiyt.nightdream.commands;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import io.github.bynoobiyt.nightdream.util.GeneralUtils;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("mvn")
public class MVN implements Command{

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a package name").queue();
		}
		String url="http://search.maven.org/solrsearch/select?q="+args[0]+"&wt=json";
		try(Scanner scan=new Scanner(new URL(url).openConnection().getInputStream())){
			String json=scan.nextLine();
			String id=GeneralUtils.getJSONString(json,"id");
			if(id.equals("?")) {
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0xdc6328)
				.addField("<:IconProvide:553870022125027329> Nothing found", "Try something different.", false);
				event.getChannel().sendMessage(builder.build()).queue();
				return;
			}
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0xdc6328)
			.setTitle("Result")
			.addField(new Field("Group ID", "`"+id.split(":")[0]+"`", true))
			.addField(new Field("Artifact ID", id.split(":")[1], true))
			.addField(new Field("Current Version", GeneralUtils.getJSONString(json, "latestVersion"), true))
			.addField(new Field("Repository", GeneralUtils.getJSONString(json, "repositoryId"), true));
			
			JDAUtils.msg(event.getChannel(), builder.build());
		}catch (IOException e) {
			JDAUtils.errmsg(event.getChannel(), "An error occurred, maybe your query is invalid");
		}
	}
	@Override
	public String help() {
		return "Allows you to view info about a maven artifact";
	}
	
}
