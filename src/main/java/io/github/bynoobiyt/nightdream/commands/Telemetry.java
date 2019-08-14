package io.github.bynoobiyt.nightdream.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import java.util.Properties;

import io.github.bynoobiyt.nightdream.core.CommandHandler;
import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("telemetry")
public class Telemetry implements Command {
	private static final Properties TELEMETRY_DATA=new Properties();
	static {
		try(Reader reader=new FileReader(new File(BotData.DATA_DIR,"telemetry.properties"))){
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
		try(Writer writer=new FileWriter(new File(BotData.DATA_DIR,"telemetry.properties"))){
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
		event.getChannel().sendMessage(sb.toString()).complete();
	}

	@Override
	public String help() {
		return null;
	}

}
