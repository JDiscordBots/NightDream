package io.github.jdiscordbots.nightdream.commands;

import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Interface for Commands<br>
 * @author Daniel Schmid
 */
public interface Command {
	/**
	 * returns if the Command is blocked or something
	 * @param args the Command-Arguments
	 * @param event The {@link GuildMessageReceivedEvent} of the incoming {@link Message}
	 * @return true if Command should be executed, else false
	 */
	public default boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		return true;
	}
	/**
	 * The Execution of the Command
	 * @param args the Command-Arguments
	 * @param event The {@link GuildMessageReceivedEvent} of the incoming {@link Message}
	 */
	public void action(String[] args, GuildMessageReceivedEvent event);
	/**
	 * after Command execution
	 * @param success has the command been executed?
	 * @param event The {@link GuildMessageReceivedEvent} of the incoming {@link Message}
	 */
	public default void executed(boolean success, GuildMessageReceivedEvent event) {
		NDLogger.logWithModule(LogType.DEBUG, "Command System","Command "+event.getMessage().getContentDisplay()+" was executed "+(success?"successfully":" but an error occured"));
		//Telemetry.addTelemetry(getClass());
	}
	/**
	 * help for the Command<br>
	 * @return help String
	 */
	public String help();
	
	/**
	 * permissions needed in order to execute this command
	 * @return permission summary
	 */
	public default String permNeeded() {
		return "<none>";
	}
	
	public CommandType getType();
	
	public enum CommandType{
		UTIL,FUN,CONFIG,META,IMAGE;
		public String getDisplayName() {
			char[] name=name().toLowerCase().toCharArray();
			name[0]=Character.toUpperCase(name[0]);
			return new String(name);
		}
	}
}
