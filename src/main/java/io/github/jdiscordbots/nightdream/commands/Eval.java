/*
 * Copyright (c) JDiscordBots 2019
 * File: Eval.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import io.github.jdiscordbots.nightdream.util.JDAUtils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@BotCommand("eval")
public class Eval implements Command {
	private Exception lastErr;
	public abstract static class Sandbox{
		protected Exception err;
		protected GuildMessageReceivedEvent event;
		protected JDA jda;
		protected Guild guild;
		protected GuildChannel channel;
		protected Message message;
		public Sandbox(GuildMessageReceivedEvent event) {
			this.event=event;
			jda=event.getJDA();
			guild=event.getGuild();
			channel=event.getChannel();
			message=event.getMessage();
		}
		public abstract Object execute() throws Exception;//NOSONAR
	}
	private Object compileAndEvaluate(String code,GuildMessageReceivedEvent event)throws Exception {
		ClassPool pool=ClassPool.getDefault();
		pool.insertClassPath(new LoaderClassPath(Eval.class.getClassLoader()));
		CtClass superClass=pool.getCtClass(Eval.class.getCanonicalName()+"$"+Sandbox.class.getSimpleName());
		CtClass cl=pool.makeClass(UUID.randomUUID().toString(), superClass);
		pool.importPackage("java.util.stream");
		pool.importPackage("io.github.jdiscordbots.nightdream.util");
		pool.importPackage("java.util");
		pool.importPackage("net.dv8tion.jda.api");
		pool.importPackage("net.dv8tion.jda.api.entities");
		pool.importPackage("org.json");
		cl.defrost();
		CtMethod method = CtNewMethod.delegator(superClass.getDeclaredMethod("execute"), cl);
		method.setBody("{if(true){\n"+code+"\n}return null;}");
		cl.addMethod(method);
		Class<?> clazz = cl.toClass(Eval.class.getClassLoader(),Eval.class.getProtectionDomain());
		Sandbox instance= (Sandbox) clazz.getConstructor(event.getClass()).newInstance(event);
		instance.err=lastErr;
		return instance.execute();
	}
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	private void exec(GuildMessageReceivedEvent event,String script) {
		try {
			long time=System.nanoTime();
			Object result;
			result=compileAndEvaluate(script, event);
			time=System.nanoTime()-time;
			if(result != null&&result.toString().contains(BotData.getToken())) {
				JDAUtils.tokenLeakAlert(event.getAuthor());
			}else {
				onSuccess(result,event,time);
			}
		} catch (Exception e) {
			lastErr=e;
			onError(e,event);
		}
	}
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
        int index=event.getMessage().getContentRaw().indexOf(' ');
        String script=index==-1?"":event.getMessage().getContentRaw().substring(index+1);
        if (script.contains("getToken")) {
        	JDAUtils.tokenLeakAlert(event.getAuthor());
		}else {
			new Thread(()->exec(event,script)).start();
		}
	}
	
	protected void onSuccess(Object result,GuildMessageReceivedEvent event,long time) {
		EmbedBuilder eb=new EmbedBuilder();
		eb.setFooter((result==null?"null":result.getClass().getCanonicalName())+" | "+time+"ns");
		if (result != null) {
			String text="```java\n"+result.toString()+"\n```";
			if(text.length()>=2000) {
				eb.setDescription("As the output was over 2000 characters, it was exported into a text file.");
				event.getChannel().sendMessage(eb.build()).addFile(result.toString().getBytes(StandardCharsets.UTF_8), "result.txt").queue();
				eb=null;
			}else {
				eb.setDescription(text);
			}
		}
		if(eb!=null) {
			event.getChannel().sendMessage(eb.build()).queue();
		}
	}
	protected void onError(Exception e,GuildMessageReceivedEvent event) {
		
		//
		try(StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw)){
			if(e instanceof CannotCompileException) {
				pw.println(((CannotCompileException) e).getReason());
			}else {
				e.printStackTrace(pw);
			}
			
			String exStr = sw.getBuffer().toString();
			int len = exStr.length();
			if(len > 1000) {
				len = 1000;
			}
			event.getChannel().sendMessage("`ERROR`\n```java\n" + exStr.substring(0, len) + "\n```").queue();
			NDLogger.logWithModule(LogType.INFO, "eval", "error: ", e);
		} catch (IOException ignored) {
			NDLogger.logWithoutModule(LogType.ERROR, "Error within incorrect user input/eval execution error handling", e);
		}
	}
	@Override
	public String help() {
		return "Evaluates Code";
	}

	@Override
    public String permNeeded() {
    	return "Bot-Admin";
    }
	
	@Override
	public CommandType getType() {
		return CommandType.META;
	}
}
