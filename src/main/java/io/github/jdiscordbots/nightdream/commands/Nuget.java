package io.github.jdiscordbots.nightdream.commands;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("nuget")
public class Nuget implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> I need a package name").queue();
		}
		String url="https://azuresearch-usnc.nuget.org/query?q="+args[0]+"&take=1"+args[0];
		try(Scanner scan=new Scanner(new BufferedInputStream(new URL(url).openConnection().getInputStream()), StandardCharsets.UTF_8.name())){
			JSONObject jsonObj=new JSONObject(scan.nextLine());
			if(jsonObj.getInt("totalHits")>0) {
				JSONObject data=jsonObj.getJSONArray("data").getJSONObject(0);
				EmbedBuilder builder=new EmbedBuilder();
				builder.setColor(0x004980);
				builder.setTitle("Result");
				builder.addField("Name",data.getString("title"),true);
				builder.addField("Description", data.getString("description"), true);
				builder.addField("Namespace", "`"+data.getString("id")+"`", true);
				builder.addField("Current Version", data.getString("version"), true);
				builder.addField("Authors", data.getJSONArray("authors").join(", "), true);
				builder.addField("Tags", "`"+data.getJSONArray("tags").join(", ")+"`", true);
				builder.addField("Verified", String.valueOf(data.getBoolean("verified")), true);
				builder.addField("Downloads", String.valueOf(data.getInt("totalDownloads")), true);
				builder.setThumbnail(data.getString("iconUrl"));
				event.getChannel().sendMessage(builder.build()).queue();
			}else {
				event.getChannel().sendMessage(
					new EmbedBuilder()
					.setColor(0x004980)
					.setTitle("<:IconProvide:553870022125027329> Nothing found")
					.setDescription("Try something different.").build()
				).queue();
			}
		}catch (IOException e) {
			event.getChannel().sendMessage("This didn't work...").queue();
		}
	}

	@Override
	public String help() {
		return "Get Nuget package info (mvn is better)";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}

}
