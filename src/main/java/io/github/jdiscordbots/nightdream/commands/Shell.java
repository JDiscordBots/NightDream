package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.util.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* TODO: 23.12.2019 Check if token is in output (see eval commit) */
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
		boolean inQuoute = false;
		boolean finished=false;
		for (int i = start; i < end; i++) {
			String arg=args[i];
			if(!inQuoute) {
				switch (arg) {
				case "|":
					cmd.pipeRedirection = parse(args, i + 1, end);
					cmd.sendResponse=cmd.sendResponse&&cmd.pipeRedirection.sendResponse;
					finished=true;
					break;
				case ">":
					cmd.outRedirection=args[i+1];
					finished=true;
					break;
				case "2>":
					cmd.errRedirection=args[i+1];
					finished=true;
					break;
				case "&":
					if(i==end-1) {
						cmd.sendResponse=false;
					}
					break;
				default:
					inQuoute=addArgumentSupportQuoting(cmdBuilder,inQuoute,arg,finished);
				}
			}else {
				inQuoute=addArgumentSupportQuoting(cmdBuilder,inQuoute,arg,finished);
			}
		}
		return cmd;
	}
	private boolean addArgumentSupportQuoting(List<String> cmdBuilder,boolean inQuoute,String arg,boolean finished) {
		if(!finished) {
			if (inQuoute) {
				
				if (arg.endsWith("\"")) {
					inQuoute = false;
					arg=arg.substring(0,arg.length()-1);
				}
				cmdBuilder.add(cmdBuilder.remove(cmdBuilder.size()-1).concat(" ").concat(arg.substring(0,arg.length()-1)));//NOSONAR
			} else {
				if (arg.startsWith("\"")&&!arg.endsWith("\"")) {
					inQuoute = true;
					arg = arg.substring(1);
				}
				cmdBuilder.add(arg);
			}
		}
		return inQuoute;
	}
	private Process startProcess(EmbedBuilder eb,TextChannel tc,ShellCommand cmd) throws IOException {
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
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		if(args.length<1) {
			event.getChannel().sendMessage("Please specify a shell command.").queue();
			return;
		}
		EmbedBuilder eb=new EmbedBuilder();
		ShellCommand cmd=parse(args,0,args.length);
		appendField(eb,"Command",null,cmd.originalCommand);
		try {
			startProcess(eb,event.getChannel(),cmd);
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
