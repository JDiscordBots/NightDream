package io.github.bynoobiyt.nightdream.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public final class TriviaListener extends ListenerAdapter {
	private static TriviaListener listener;
	private Map<String, Set<List<String>>> questions=new HashMap<>();
	private Timer timer=new Timer();
	private JDA jda;
	private TriviaListener(JDA jda) {
		this.jda=jda;
	}
	private static TriviaListener getListener(JDA jda) {
		synchronized(TriviaListener.class) {
			if(listener==null) {
				listener=new TriviaListener(jda);
				jda.addEventListener(listener);
			}
			return listener;
		}
		
	}
	
	public static  void addQuestion(final TextChannel chan,String correct, String[] wrong) {
		synchronized(TriviaListener.class) {
			for (int i = 0; i < wrong.length; i++) {
				wrong[i]=wrong[i].toLowerCase();
			}
			final List<String> question=new ArrayList<>(Arrays.asList(wrong));
			question.add(correct.toLowerCase());
			getListener(chan.getJDA());
			Set<List<String>> questions;
			if(listener.questions.containsKey(chan.getId())) {
				questions=listener.questions.get(chan.getId());
				
			}else {
				questions=new HashSet<>();
				listener.questions.put(chan.getId(), questions);
			}
			questions.add(question);
			listener.timer.schedule(new TimerTask() {
				@Override
				public synchronized void run() {
					if(listener!=null&&listener.questions.containsKey(chan.getId())&&listener.questions.get(chan.getId()).contains(question)) {
						chan.sendMessage("Nobody got the answer this time. Sad.").queue();
						
						listener.questions.get(chan.getId()).remove(question);
						if(listener.questions.get(chan.getId()).isEmpty()) {
							listener.questions.remove(chan.getId());
							if(listener.questions.isEmpty()) {
								listener.jda.removeEventListener(listener);
								listener=null;
							}
						}
					}
				}
			}, 30000);
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(questions.containsKey(event.getChannel().getId())) {
			synchronized (getClass()) {
				List<String> q=null;
				for (List<String> question : questions.get(event.getChannel().getId())) {
					if(question.contains(event.getMessage().getContentDisplay().toLowerCase())) {
						if(event.getMessage().getContentDisplay().equalsIgnoreCase(question.get(question.size()-1))) {
							event.getChannel().sendMessage(event.getMember().getEffectiveName()+" got it!").queue();
						}else {
							event.getChannel().sendMessage("wrong!").queue();
						}
						q=question;
					}
				}
				if(q!=null) {
					questions.get(event.getChannel().getId()).remove(q);
					if(questions.get(event.getChannel().getId()).isEmpty()) {
						questions.remove(event.getChannel().getId());
						if(questions.isEmpty()) {
							timer.cancel();
							timer.purge();
							jda.removeEventListener(this);
							TriviaListener.listener=null;
						}
					}
				}
			}
		}
	}
}
