/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: Shell.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		} else {
			return ret;
		}
	}
	private void appendField(EmbedBuilder builder,String name,String info,String text) {
		if(text!=null) {
			builder.appendDescription("**"+name+"**"+(info==null?"":("(`"+info+"`)"))+": "+FIELD_START+(text.length()>500?text.substring(0, 500):text)+FIELD_END);
		}
	}
	private static class ShellCommand{
		private List<String> cmd;
		private String originalCommand="";
		private String outRedirection=null;
		private String errRedirection=null;
		private boolean sendResponse=true;
		private ShellCommand pipeRedirection=null;
		public ShellCommand(String cmd) {
			super();
			this.originalCommand = cmd;
			this.cmd=Arrays.asList(cmd.split(" "));
		}
	}
	private ShellCommand parse(String[] args,int start,int end) {
		ShellCommand cmd=new ShellCommand(String.join(" ",args));
		List<String> cmdBuilder=new ArrayList<>(cmd.originalCommand.length());
		cmd.cmd=cmdBuilder;
		for (int i = start; i < end; i++) {
			String arg=args[i];
			switch (arg) {
			case "|":
				cmd.pipeRedirection = parse(args, i + 1, end);
				cmd.sendResponse=cmd.sendResponse&&cmd.pipeRedirection.sendResponse;
				break;
			case ">":
				cmd.outRedirection=args[i+1];
				break;
			case "2>":
				cmd.errRedirection=args[i+1];
				break;
			case "&":
				if(i==end-1) {
					cmd.sendResponse=false;
				}
				break;
			default:
				cmdBuilder.add(arg);
			}
		}
		return cmd;
	}
	private Process startProcess(EmbedBuilder eb,TextChannel tc,ShellCommand cmd,User executor) throws IOException {
		ProcessBuilder pBuilder=new ProcessBuilder(cmd.cmd);
		
		if(cmd.outRedirection!=null) {
			pBuilder.redirectOutput(new File(cmd.outRedirection));
		}
		if(cmd.errRedirection!=null) {
			pBuilder.redirectError(new File(cmd.errRedirection));
		}
		Process p;
		Process lastProcess;
		if(cmd.pipeRedirection!=null) {
			ProcessBuilder pipeBuilder=new ProcessBuilder(cmd.pipeRedirection.cmd);
			pBuilder.redirectOutput(pipeBuilder.redirectInput());
			lastProcess=pipeBuilder.start();
			p=pBuilder.start();
		}else {
			p=pBuilder.start();
			lastProcess=p;
		}
		if(cmd.sendResponse) {
			startAutoKill(p,tc);
			threadPool.execute(()->{
                try{
                	int exitCode=p.waitFor();
	            	eb.setFooter("Finished with exit code "+exitCode+" under "+OS_NAME);
	            	eb.setColor(exitCode==0?Color.GREEN:Color.RED);
	            	
	                String out=readFromInputStream(lastProcess.getInputStream());
	                String err=readFromInputStream(lastProcess.getErrorStream());
	                testToken(out,executor);
	                testToken(err,executor);
	                appendField(eb,"Output","stdout",out);
	                appendField(eb,"Errors","stderr",err);
	                
	            }catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				tc.sendMessage(eb.build()).queue();
			});
		}
		return p;
	}
	private void testToken(String toValidate,User user) {
		if(toValidate != null&&toValidate.contains(BotData.getToken())) {
			JDAUtils.tokenLeakAlert(user);
		}
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length<1) {
			event.getChannel().sendMessage("Please specify a shell command.").queue();
			return;
		}
		if(String.join("", args).toLowerCase().contains("token")) {
			JDAUtils.tokenLeakAlert(event.getAuthor());
		}
		EmbedBuilder eb=new EmbedBuilder();
		ShellCommand cmd=parse(args,0,args.length);
		appendField(eb,"Command",null,cmd.originalCommand);
		try {
			startProcess(eb,event.getChannel(),cmd,event.getAuthor());
		} catch (IOException e) {
			eb.appendDescription("Cannot start Process under "+OS_NAME);
			if(e.getMessage()!=null) {
				eb.appendDescription("\n```bash\n"+e.getMessage()+"\n```");
			}
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
