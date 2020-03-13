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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import bsh.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

@BotCommand("eval")
public class Eval implements Command {

	private static final String LATEST_EXCEPTION_KEY_NAME="err";
	
	//bsh
	private static Interpreter shell=new Interpreter();
	
	//Java Compiler(only in JDK)
	private static JavaCompiler compiler;
	private static File parentDir;
	
	static {
		try {
			compiler=ToolProvider.getSystemJavaCompiler();
			parentDir=Files.createTempDirectory("NightDreamEval").toFile();
		} catch (IOException e) {
			compiler=null;
			parentDir=null;
		}
	}
	
	private Object compileAndEvaluate(String className,String code,Map<String,Object> params)throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		File sourceFile = new File(parentDir,className+".java");
		if(!sourceFile.exists()) {
			try(Writer writer=new BufferedWriter(new FileWriter(sourceFile))){
				writer.write("public class ");
				writer.write(className);
				writer.write("{public static Object eval(");
				boolean[] hasPrev= {false};//array because lamda can only use effectively final variables
				params.forEach((k,v)->{
					try {
						if(hasPrev[0]) {
							writer.write(',');
						}else {
							hasPrev[0]=true;
						}
						writer.write(v.getClass().getCanonicalName());
						writer.write(' ');
						writer.write(k);
						
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
				writer.write("){if(true){\n"+code+"\n}return null;}}");
			}catch(UncheckedIOException e) {
				throw e.getCause();
			}
			try(StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null)){
				standardFileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(parentDir));
				Iterable<? extends JavaFileObject> compilationUnits = standardFileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
				compiler.getTask(null, standardFileManager, null,null,null, compilationUnits).call();
			}
		}
		try(URLClassLoader loader = URLClassLoader.newInstance(new URL[] {parentDir.toURI().toURL()}, Eval.class.getClassLoader())){
			Class<?> clazz = loader.loadClass(className);
			Method method = clazz.getDeclaredMethod("eval",params.values().stream().map(Object::getClass).toArray(Class<?>[]::new));
			return method.invoke(null,params.values().toArray());
		}
	}
	private String getClassName(String code, Map<String, Object> params) {
		try {
			MessageDigest digest=MessageDigest.getInstance("SHA-256");
			digest.update(params.keySet().toString().getBytes(StandardCharsets.UTF_8));
			return binToString(digest.digest(code.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	private String binToString(byte[] data) {
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if(data[i]<0) {
				data[i]*=-1;
			}
			do{
				sb.append((char)('A'+data[i]%26));
				data[i]/=26;
			}while(data[i]>0);
		}
		return sb.toString();
	}
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	private void exec(GuildMessageReceivedEvent event,String script) {
		try {
			if(compiler==null) {
				for (Entry<String, Object> entry : params.entrySet()) {
					shell.set(entry.getKey(), entry.getValue());
				}
			}
			long time=System.nanoTime();
			Object result;
			if(compiler!=null) {
				result=compileAndEvaluate(getClassName(script,params), script, params);
			}else {
				
				result = shell.eval(script);
			}
			time=System.nanoTime()-time;
			if(result != null&&result.toString().contains(BotData.getToken())) {
				JDAUtils.tokenLeakAlert(event.getAuthor());
			}else {
				onSuccess(result,event,time);
			}
		} catch (Exception e) {
			params.put(LATEST_EXCEPTION_KEY_NAME, e);
			onError(e,event);
		}
	}
	private Map<String,Object> params=new HashMap<>();
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		params.put("event", event);
    	params.put("jda", event.getJDA());
    	params.put("guild", event.getGuild());
    	params.put("channel", event.getChannel());
    	params.put("message", event.getMessage());
        
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
		e.setStackTrace(new StackTraceElement[0]);
		try(StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw)){
			e.printStackTrace(pw);
			String exStr = sw.getBuffer().toString();
			int len = exStr.length();
			if(len > 1000) {
				len = 1000;
			}
			event.getChannel().sendMessage("`ERROR`\n```java\n" + exStr.substring(0, len) + "\n```").queue();
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
