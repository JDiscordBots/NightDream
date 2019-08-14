package io.github.bynoobiyt.nightdream.commands;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import io.github.bynoobiyt.nightdream.util.GeneralUtils;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("yn")
public class YN implements Command {

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		try(Scanner scan=new Scanner(new URL("https://yesno.wtf/api").openConnection().getInputStream())){
			String json=scan.nextLine();
			
			String answer=GeneralUtils.getJSONString(json, "answer");
			answer=Character.toUpperCase(answer.charAt(0))+answer.substring(1);
			String url=GeneralUtils.getJSONString(json, "image");
			event.getChannel().sendMessage(
					new EmbedBuilder()
					.setColor(0x212121)
					.setTitle(answer)
					.setImage(url)
					.build()).queue();
		} catch (IOException e) {
			JDAUtils.errmsg(event.getChannel(), "something went wrong.");
			e.printStackTrace();
		}
	}

	@Override
	public String help() {
		return "Answer a yes/no question";
	}

}
