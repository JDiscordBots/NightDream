/*
 * Copyright (c) JDiscordBots 2019
 * File: Telemetry.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.core.CommandHandler;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

//@BotCommand("telemetry")
public class Telemetry implements Command {
	private static final Properties TELEMETRY_DATA=new Properties();
	static {
		try(Reader reader=new InputStreamReader(new BufferedInputStream(new FileInputStream(new File(BotData.DATA_DIR,"telemetry.properties"))),StandardCharsets.UTF_8)){
			TELEMETRY_DATA.load(reader);
		} catch (IOException e) {
			//ignore
		}
	}
	
	public static void addTelemetry(Class<? extends Command> c) {
		String name=c.getSimpleName();
		String telStr=TELEMETRY_DATA.getProperty(name);
		if(telStr==null) {
			TELEMETRY_DATA.setProperty(name, "1");
		}else {
			int telemetry=Integer.parseInt(telStr.trim());
			TELEMETRY_DATA.setProperty(name, String.valueOf(telemetry+1));
		}
		try(Writer writer=new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(new File(BotData.DATA_DIR,"telemetry.properties"))), StandardCharsets.UTF_8)){
			TELEMETRY_DATA.store(writer,"Telemetry Data");
		} catch (IOException e) {
			//ignore
		}
	}

	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		StringBuilder sb=new StringBuilder();
		for(Command cmd:CommandHandler.getCommands().values()) {
			sb.append(BotData.getPrefix(event.getGuild())).append(cmd.getClass().getSimpleName().toLowerCase());
			if(TELEMETRY_DATA.containsKey(cmd.getClass().getSimpleName())) {
				sb.append(" used ").append(TELEMETRY_DATA.getProperty(cmd.getClass().getSimpleName())).append(" times in total\n");
			}else {
				sb.append(" not used at all\n");
			}
		}
		event.getChannel().sendMessage(sb.toString()).queue();
	}

	@Override
	public String help() {
		return "shows telemetry data";
	}

}
