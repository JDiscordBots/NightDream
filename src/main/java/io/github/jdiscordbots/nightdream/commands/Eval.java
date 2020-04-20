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
import javassist.NotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
import java.util.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

@BotCommand("eval")
public class Eval implements Command {
	
	private static ClassPool pool;
	private static CtClass superClass;
	
	private Throwable lastErr=new Exception();
	
	//region Java Compiler(only in JDK)
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
	private Object compileAndEvaluateJdkCompiler(String code,GuildMessageReceivedEvent event) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, CannotCompileException, NoSuchAlgorithmException {
		Map<String,Object> params=new HashMap<>();
		params.put("event", event);
    	params.put("jda", event.getJDA());
    	params.put("guild", event.getGuild());
    	params.put("channel", event.getChannel());
    	params.put("message", event.getMessage());
    	params.put("err", lastErr);
		return compileAndEvaluateJdkCompiler(getClassName(code,params), code, params);
	}
	private Object compileAndEvaluateJdkCompiler(String className,String code,Map<String,Object> params)throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, CannotCompileException {
		File sourceFile = new File(parentDir,className+".java");
		String errData="This file has previously been compiled and there was a compilation error";
		if(!sourceFile.exists()) {
			try(Writer writer=new BufferedWriter(new FileWriter(sourceFile))){
				writer.write("import io.github.jdiscordbots.nightdream.util.*;");
				writer.write("import net.dv8tion.jda.api.*;");
				writer.write("import net.dv8tion.jda.api.entities.*;");
				writer.write("import org.json.*;");
				writer.write("import java.util.stream.*;");
				writer.write("import java.util.*;");
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
				writer.write("){if(true){\n"+code+"\n/**/}return null;}}");
			}catch(UncheckedIOException e) {
				throw e.getCause();
			}
			try(StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
					StringWriter out=new StringWriter()){
				standardFileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(parentDir));
				Iterable<? extends JavaFileObject> compilationUnits = standardFileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile));
				compiler.getTask(out, standardFileManager, null,Arrays.asList("-classpath",System.getProperty("java.class.path")),null, compilationUnits).call();
				errData=out.toString();
			}
		}
		try(URLClassLoader loader = URLClassLoader.newInstance(new URL[] {parentDir.toURI().toURL()}, Eval.class.getClassLoader())){
			Class<?> clazz = loader.loadClass(className);
			Method method = clazz.getDeclaredMethod("eval",params.values().stream().map(Object::getClass).toArray(Class<?>[]::new));
			return method.invoke(null,params.values().toArray());
		}catch(ClassNotFoundException e) {
			throw new CannotCompileException(errData);
		}
	}
	private String getClassName(String code, Map<String, Object> params) throws NoSuchAlgorithmException {
		MessageDigest digest=MessageDigest.getInstance("SHA-256");
		digest.update(params.keySet().toString().getBytes(StandardCharsets.UTF_8));
		return binToString(digest.digest(code.getBytes(StandardCharsets.UTF_8)));
	}
	private String binToString(byte[] data) {
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if(data[i]<0) {
				data[i]*=-1;
			}
			do{
				sb.append((char)('A'+Math.abs(data[i])%26));
				data[i]/=26;
			}while(data[i]>0);
		}
		return sb.toString();
	}
	
	//endregion
	
	//region javassist
	public abstract static class Sandbox{
		protected Throwable err;
		protected GuildMessageReceivedEvent event;
		protected JDA jda;
		protected Guild guild;
		protected TextChannel channel;
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
	
	
	
	private static void init() throws NotFoundException {
		ClassPool p=ClassPool.getDefault();
		p.insertClassPath(new LoaderClassPath(Eval.class.getClassLoader()));
		CtClass cl=p.getCtClass(Eval.class.getCanonicalName()+"$"+Sandbox.class.getSimpleName());
		p.importPackage("io.github.jdiscordbots.nightdream.util");
		p.importPackage("java.util");
		p.importPackage("net.dv8tion.jda.api");
		p.importPackage("net.dv8tion.jda.api.entities");
		p.importPackage("org.json");
		
		Eval.pool=p;
		Eval.superClass=cl;
	}
	private Object compileAndEvaluateJavassist(String code,GuildMessageReceivedEvent event)throws Exception {
		init();
		CtClass cl=pool.makeClass(UUID.randomUUID().toString(), superClass);
		CtMethod method = CtNewMethod.delegator(superClass.getDeclaredMethod("execute"), cl);
		method.setBody("{if(true){\n"+code+"\n/**/}return null;}");
		cl.addMethod(method);
		Class<?> clazz = cl.toClass(Eval.class.getClassLoader(),Eval.class.getProtectionDomain());
		Sandbox instance= (Sandbox) clazz.getConstructor(event.getClass()).newInstance(event);
		instance.err=lastErr;
		return instance.execute();
	}
	//endregion
	
	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return JDAUtils.checkOwner(event,args!=null);	
	}
	private void exec(GuildMessageReceivedEvent event,String script) {
		try {
			long time=System.nanoTime();
			Object result;
			if(compiler!=null) {
				result=compileAndEvaluateJdkCompiler(script, event);
			}else {
				result=compileAndEvaluateJavassist(script, event);
			}
			time=System.nanoTime()-time;
			if(result != null&&result.toString().contains(BotData.getToken())) {
				JDAUtils.tokenLeakAlert(event.getAuthor());
			}else {
				onSuccess(result,event,time);
			}
		} catch (VerifyError|Exception e) {
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
	protected void onError(Throwable e,GuildMessageReceivedEvent event) {
		try(StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw)){
			if(e instanceof CannotCompileException) {
				pw.println(((CannotCompileException) e).getReason());
			}else if(e instanceof VerifyError){
				pw.print("Invalid return type - The method must either return an object or nothing.");
			}else {
				e.printStackTrace(pw);
			}
			
			String exStr = sw.getBuffer().toString();
			int len = exStr.length();
			if(len > 1000) {
				len = 1000;
			}
			event.getChannel().sendMessage("`ERROR`\n```java\n" + exStr.substring(0, len) + "\n```").queue();
			NDLogger.logWithModule(LogType.DEBUG, "eval", "error: ", e);
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
