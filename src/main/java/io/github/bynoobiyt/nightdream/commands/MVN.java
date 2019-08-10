package io.github.bynoobiyt.nightdream.commands;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("mvn")
public class MVN implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(args.length==0) {
			event.getTextChannel().sendMessage("<:IconProvide:553870022125027329> I need a package name").complete();
		}
		String url="http://search.maven.org/solrsearch/select?q="+args[0]+"&wt=json";
		try(Scanner scan=new Scanner(new URL(url).openConnection().getInputStream())){
			String json=scan.nextLine();
			String id=getJSONString(json,"id");
			if(id.equals("?")) {
				Utils.errmsg(event.getTextChannel(), "Not found.");
				return;
			}
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0xfb3b49)
			.setTitle("Result")
			.addField(new Field("group id", "`"+id.split(":")[0]+"`", true))
			.addField(new Field("artefact id", id.split(":")[1], true))
			.addField(new Field("Current Version", getJSONString(json, "latestVersion"), true))
			.addField(new Field("Repository", getJSONString(json, "repositoryId"), true));
			
			Utils.msg(event.getTextChannel(), builder.build(),false);
		}catch (IOException e) {
			Utils.errmsg(event.getTextChannel(), "An error occured, maybe your query is invalid");
		}
	}
	private String getJSONString(String json,String query) {
		String str="\""+query+"\":\"";
		if(json.indexOf(str)<0) {
			return "?";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf('\"', startIndex));
	}
	@Override
	public String help() {
		return "Allows you to view info about a maven artifact";
	}
	
}
