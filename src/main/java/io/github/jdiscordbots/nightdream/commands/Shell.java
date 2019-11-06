package io.github.jdiscordbots.nightdream.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
		eb.setDescription("**Command**: ```bash\n"+String.join(" ", args)+"```\n\n");
		ProcessBuilder builder=new ProcessBuilder(args);
		try {
			Process p=builder.start();
			threadPool.execute(()->{
                try(BufferedReader stdout=new BufferedReader(new InputStreamReader(p.getInputStream(),Charset.defaultCharset()));
            		BufferedReader stderr=new BufferedReader(new InputStreamReader(p.getErrorStream(),Charset.defaultCharset()))){
                	int exitCode=p.waitFor();
	            	eb.setFooter("Finished with exit code "+exitCode+" under "+System.getProperty("os.name"));
	            	eb.setColor(exitCode==0?Color.GREEN:Color.RED);
	                String out=stdout.lines().collect(Collectors.joining("\n"));
	                String err=stderr.lines().collect(Collectors.joining("\n"));
	                if(!"".equals(out)) {
	                	eb.appendDescription("**Output**(`stdout`): ```bash\n"+out+"```\n\n");
	                }
	                if(!"".equals(err)) {
	                	eb.appendDescription("**Errors**(`stderr`): ```bash\n"+err+"```\n\n");
	                }
	            }catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				}catch (UnsupportedEncodingException e) {
					NDLogger.logWithModule(LogType.ERROR, "shell", "encoding "+Charset.defaultCharset().name()+" not found");
				}catch (IOException ex) {
	            	eb.setTitle("Some weird error occured while reading input\n\n");
	            }
				event.getChannel().sendMessage(eb.build()).queue();
			});
		} catch (IOException e) {
			eb.appendDescription("Cannot start Process under "+System.getProperty("os.name"));
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
