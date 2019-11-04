package io.github.jdiscordbots.nightdream.commands;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("shell")
public class Shell implements Command {

	private ExecutorService threadPool=Executors.newCachedThreadPool();
	
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	@Override
	public String permNeeded() {
		return "Bot-Admin";
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length<1) {
			event.getChannel().sendMessage("Please specify a shell command.").queue();
			return;
		}
		EmbedBuilder eb=new EmbedBuilder();
		eb.setTitle(String.join(" ", args));
		ProcessBuilder builder=new ProcessBuilder(args);
		builder.redirectErrorStream(true);
		try {
			Process p=builder.start();
			InputStream in=p.getInputStream();
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			threadPool.execute(()->{
				try {
	                int d;
	                while ((d = in.read()) != -1) {
	                    out.write(d);
	                }
	                
	            } catch (IOException ex) {
	            	eb.setTitle("Some weird error occured while reading input\n\n");
	            }
				if(p.isAlive()) {
                	eb.setFooter("Not yet finished");
                	eb.setColor(Color.YELLOW);
                }else {
                	int exitCode=p.exitValue();
                	eb.setFooter("Finished with exit code "+exitCode);
                	eb.setColor(exitCode==0?Color.GREEN:Color.RED);
                }
                try {
					eb.appendDescription(out.toString(Charset.defaultCharset().name()));
				} catch (UnsupportedEncodingException e) {
					NDLogger.logWithModule(LogType.ERROR, "shell", "encoding "+Charset.defaultCharset().name()+" not found");
				}
				event.getChannel().sendMessage(eb.build()).queue();
			});
		} catch (IOException e) {
			eb.setDescription("Cannot start Process");
			eb.setColor(Color.RED.darker());
			event.getChannel().sendMessage(eb.build()).queue();
		}
	}

	@Override
	public String help() {
		return "execute a shell command";
	}

	@Override
	public CommandType getType() {
		return CommandType.META;
	}

}
