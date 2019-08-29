/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: Photo.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.GeneralUtils;
import io.github.bynoobiyt.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@BotCommand("photo")
public class Photo implements Command {

	private static final Logger LOG=LoggerFactory.getLogger(Photo.class);
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("<:IconProvide:553870022125027329> Search Query, please.").queue();
			return;
		}
		if("".equals(BotData.getPixaBayAPIKey())) {
			JDAUtils.errmsg(event.getChannel(),"This command is disabled because there is no API Key set.");
			LOG.warn("no Pixabay API Key provided");
			return;
		}
		event.getChannel().sendTyping();
		try(Scanner scan=new Scanner(new BufferedInputStream(new URL(
				"https://pixabay.com/api/?image_type=photo&key="+BotData.getPixaBayAPIKey()+"&q="+URLEncoder.encode(String.join(" ", args),StandardCharsets.UTF_8.name())
				).openConnection().getInputStream()), StandardCharsets.UTF_8.toString())){
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0x212121);
			String imgUrl=GeneralUtils.getJSONString(scan.nextLine(), "largeImageURL");
			if("?".equals(imgUrl)) {
				builder.setTitle("<:IconProvide:553870022125027329> Nothing found")
                .setDescription("Try something different.");
			}else {
				builder.setFooter("Results from Pixabay [https://pixabay.com]")
                .setTitle("Result")
                .setImage(imgUrl);
			}
			event.getChannel().sendMessage(builder.build()).queue();
		} catch (IOException e) {
			event.getChannel().sendMessage("<:IconX:553868311960748044> Something went badly wrong - the server did not respond! Try again **in a few minutes**.").queue();
			LOG.error("cannot load photo",e);
		}
	}

	@Override
	public String help() {
		return "Gets a photo from Pixabay";
	}

}
