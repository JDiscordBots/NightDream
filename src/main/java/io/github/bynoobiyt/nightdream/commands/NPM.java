package io.github.bynoobiyt.nightdream.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("npm")
public class NPM implements Command{

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(args.length==0) {
			event.getTextChannel().sendMessage("<:IconProvide:553870022125027329> I need a package name").complete();
		}
		String url="http://registry.npmjs.org/"+args[0];
		try(Scanner scan=new Scanner(new URL(url).openConnection().getInputStream())){
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
			.addField(new Field("name", "`"+getJSONString(json, "name")+"`", true))
			.addField(new Field("Description", getJSONString(json, "description"), true))
			.addField(new Field("Current Version", getJSONString(json, "latest"), true))
			.addField(new Field("Keywords", "`"+getMultipleJSONStrings(json, "keywords")+"`", true))
			.addField(new Field("Author", getJSONString(json, "author\":{\"name"), true))
			.addField(new Field("Scope", "`"+scope+"`", true));
			
			Utils.msg(event.getTextChannel(), builder.build(),false);
		}catch(FileNotFoundException e) {
			Utils.errmsg(event.getTextChannel(), "Not found");
		}catch (IOException e) {
			Utils.errmsg(event.getTextChannel(), "An error occured.");
			e.printStackTrace();
		}
	}
	private String getJSONString(String json,String query) {
		String str="\""+query+"\":\"";
		if(json.indexOf(str)<0) {
			return "undefined";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf('\"', startIndex));
	}
	private String getMultipleJSONStrings(String json,String query) {
		String str="\""+query+"\":[\"";
		if(json.indexOf(str)<0) {
			return "undefined";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf("\"]", startIndex)).replace("\"", "").replace(",", ", ");
	}
	@Override
	public String help() {
		return "Allows you to view info about an NPM package";
	}
	
}
