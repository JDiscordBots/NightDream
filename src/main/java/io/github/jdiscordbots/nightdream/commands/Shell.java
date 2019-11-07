package io.github.jdiscordbots.nightdream.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("shell")
public class Shell implements Command {

	private static final String FIELD_START="```bash\n";
	private static final String FIELD_END="```\n\n";
	
	private static final String OS_NAME=System.getProperty("os.name");
	
	private ExecutorService threadPool=Executors.newCachedThreadPool(r->{
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });
	private Timer timer=new Timer(true);
	
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	@Override
	public String permNeeded() {
		return "Bot-Admin";
	}
	private void startAutoKill(Process p,TextChannel chan) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(p.isAlive()) {
					chan.sendMessage("Excuse the wait, the command is still executing! The process will be killed in 10 more seconds.").queue();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							if(p.isAlive()) {
								p.destroy();
								chan.sendMessage("Process was killed after being unresponsive for 20 seconds.").queue();
							}
						}
					}, 10*1000L);
				}
			}
		}, 10*1000L);
	}
	private String readFromInputStream(InputStream is) {
		StringBuilder sb=new StringBuilder();
		try(BufferedReader reader=new BufferedReader(new InputStreamReader(is,Charset.defaultCharset()))){
			String line;
			while((line=reader.readLine())!=null) {
				sb.append(line).append('\n');
			}
		}catch (IOException ignore) {
			//handled by return if
        }
		String ret=sb.toString();
		if("".equals(ret)) {
			return null;
		}else {
			return ret;
		}
	}
	private void appendField(EmbedBuilder builder,String name,String info,String text) {
		if(text!=null) {
			builder.appendDescription("**"+name+"**"+(info==null?"":("(`"+info+"`)"))+": "+FIELD_START+(text.length()>500?text.substring(0, 500):text)+FIELD_END);
		}
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length<1) {
			event.getChannel().sendMessage("Please specify a shell command.").queue();
			return;
		}
		EmbedBuilder eb=new EmbedBuilder();
		String cmd=String.join(" ", args);
		appendField(eb,"Command",null,cmd);
		try {
			Process p=Runtime.getRuntime().exec(cmd);
			startAutoKill(p,event.getChannel());
			threadPool.execute(()->{
                try{
                	int exitCode=p.waitFor();
	            	eb.setFooter("Finished with exit code "+exitCode+" under "+OS_NAME);
	            	eb.setColor(exitCode==0?Color.GREEN:Color.RED);
	                String out=readFromInputStream(p.getInputStream());
	                String err=readFromInputStream(p.getErrorStream());
	                appendField(eb,"Output","stdout",out);
	                appendField(eb,"Errors","stderr",err);
	            }catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				event.getChannel().sendMessage(eb.build()).queue();
			});
		} catch (IOException e) {
			eb.appendDescription("Cannot start Process under "+OS_NAME);
			eb.setColor(Color.RED.darker());
			event.getChannel().sendMessage(eb.build()).queue();
		}
	}

	@Override
	public String help() {
		return "execute a shell command ("+OS_NAME+")";
	}

	@Override
	public CommandType getType() {
		return CommandType.META;
	}

}
