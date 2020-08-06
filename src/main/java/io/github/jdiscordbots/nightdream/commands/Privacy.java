package io.github.jdiscordbots.nightdream.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("privacy")//TODO tests, wiki
public class Privacy implements Command{
	private static final Logger LOG=LoggerFactory.getLogger(Privacy.class);
	private static final Pattern HEADER_REGEX=Pattern.compile("#+\\s+(.+)");
	private static final Pattern INFO_REGEX=Pattern.compile(">\\s+(.+)");
	private static final Pattern EMPTY_REGEX=Pattern.compile("\\s*");
	public void action(String[] args, GuildMessageReceivedEvent event) {
		boolean hasStarted=false;
		EmbedBuilder eb=new EmbedBuilder();
		StringBuilder currentHeader=new StringBuilder();
		StringBuilder currentTextBuilder=new StringBuilder();
		try(BufferedReader reader=new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("privacy-policy.md"), StandardCharsets.UTF_8))){
			String line;
			while((line=reader.readLine())!=null) {
				Matcher headerMatcher= HEADER_REGEX.matcher(line);
				Matcher infoMatcher= INFO_REGEX.matcher(line);
				if(headerMatcher.matches()) {
					if(hasStarted) {
						addFieldToBuilderIfNonEmpty(eb,currentTextBuilder,currentHeader);
						currentHeader.setLength(0);
						currentHeader.append(headerMatcher.group(1));
						continue;
					}else {
						eb.setTitle(headerMatcher.group(1));
					}
					
				}else if(!hasStarted&&infoMatcher.matches()) {
					eb.setFooter(infoMatcher.group(1));
				}else if(!hasStarted&&!EMPTY_REGEX.matcher(line).matches()&&!line.trim().equals("---")) {
					hasStarted=true;
				}
				if(hasStarted) {
					currentTextBuilder.append(line).append('\n');
				}
			}
			addFieldToBuilderIfNonEmpty(eb,currentTextBuilder,currentHeader);
			event.getChannel().sendMessage(eb.build()).queue();
		} catch (IOException e) {
			LOG.error("cannot read privacy policy", e);
		}
	}
	private void addFieldToBuilderIfNonEmpty(EmbedBuilder eb,StringBuilder currentTextBuilder,StringBuilder currentHeader) {
		if(currentTextBuilder.length()!=0) {
			eb.addField(currentHeader.toString(), currentTextBuilder.toString(), false);
			currentTextBuilder.setLength(0);
		}
	}
	public String help() {
		return "Displays the privacy policy";
	}

	public CommandType getType() {
		return CommandType.META;
	}

}
