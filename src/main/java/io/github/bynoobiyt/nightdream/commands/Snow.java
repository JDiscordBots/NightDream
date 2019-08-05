package io.github.bynoobiyt.nightdream.commands;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.github.bynoobiyt.nightdream.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BotCommand("snow")
public class Snow implements Command {

	private static int increment=0;
	private static final long EPOCH=1420070400000L;//Discord epoch/1.1.2015 0:00
	
	@Override
	public boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return Utils.checkOwner(event);	
	}
	
	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		if(args.length==0) {
			event.getChannel().sendMessage("`"+generate()+"` made `"+new Date()+"`").complete();
		}else {
			EmbedBuilder eb=new EmbedBuilder();
			eb.setTitle(args[0]);
			eb.setColor(0x212121);
			String binStr=fillWithZerosBefore(64,Long.toBinaryString(Long.valueOf(args[0])));
			eb.addField("Binary",binStr , false);
			eb.addField("Date", new Date(Long.valueOf(binStr.substring(0,42),2)+EPOCH).toString(), false);
			eb.addField("Increment", Integer.valueOf(binStr.substring(52,64),2).toString(), false);
			eb.addField("Worker, Process ID", args[0]+" has worker ID "+Integer.valueOf(binStr.substring(42,47),2)+" with process ID "+Integer.valueOf(binStr.substring(47,52),2), false);
			event.getTextChannel().sendMessage(eb.build()).complete();
		}
	}
	@Override
	public String help() {
		return null;
	}
	private String generate() {
		long diff=System.currentTimeMillis()-EPOCH;
		String timeBinStr=Long.toBinaryString(diff);
		String timeStampStr=fillWithZerosBefore(42,timeBinStr);
		String workerAndProcessIDs="0000100000";
		String binStr=timeStampStr+workerAndProcessIDs+fillWithZerosBefore(12,Integer.toBinaryString((increment++)%4096));
		return Long.valueOf(binStr, 2).toString();
	}
	private String fillWithZerosBefore(int len,String original) {
		char[] data=new char[len];
		int strlen=original.length();
		Arrays.fill(data,0,data.length-strlen, '0');
		for(int i=data.length-strlen,j=0;i<data.length;i++,j++) {
			data[i]=original.charAt(j);
		}
		return new String(data);
	}
}
