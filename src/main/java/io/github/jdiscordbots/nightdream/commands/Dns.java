package io.github.jdiscordbots.nightdream.commands;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("dns")
public class Dns implements Command {
	private static final String UNKNOWN_MSG="<unknown>";

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> Unknown Resolve Target").queue();
		}else {
			try(Scanner scan=new Scanner(new URL("https://dns.google.com/resolve?type=PTR&name="+String.join(" ", args)).openStream(),StandardCharsets.UTF_8.name())){
				JSONObject json=new JSONObject(scan.nextLine());
				if(json.getInt("Status")==0) {
					JSONObject qObj = json.getJSONArray("Question").getJSONObject(0);
					String question = qObj.getString("name");
					question=question.substring(0, question.length()-1);
					EmbedBuilder eb=new EmbedBuilder().setColor(0x212121)
							.setTitle(question+" (type "+qObj.getInt("type")+") resolves to:");
					JSONArray authority=json.getJSONArray("Authority");
					if(authority.length()>0) {
						JSONObject auth=authority.getJSONObject(0);
						eb.addField("Type", String.valueOf(auth.getInt("type")),false);
						eb.addField("Time to live (TTL)", String.valueOf(auth.getInt("TTL")), false);
						
						String[] data=auth.getString("data").split(Pattern.quote(". "));
						eb.addField("Nameserver", data[0], false);
						eb.addField("DNS Hostmaster", data[1], false);
						eb.addField("Mystery Text", data[2], false);
					}else {
						eb.addField("Type", UNKNOWN_MSG, false);
						eb.addField("Time to live (TTL)", UNKNOWN_MSG, false);
						eb.addField("Nameserver", UNKNOWN_MSG, false);
						eb.addField("DNS Hostmaster", UNKNOWN_MSG, false);
						eb.addField("Mystery Text", UNKNOWN_MSG, false);
					}
					eb.setFooter(json.getString("Comment"));
					event.getChannel().sendMessage(eb.build()).queue();
				}else {
					event.getChannel().sendMessage("Your query is unresolvable.\nTry with a different one?").queue();
				}
			} catch (IOException e) {
				event.getChannel().sendMessage("Unknown Error").queue();
			}
		}
	}

	@Override
	public String help() {
		return "Resolve an address";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}

}
