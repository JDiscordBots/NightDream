package io.github.bynoobiyt.nightdream.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Interface for Commands<br>
 * @author Daniel Schmid
 */
public interface Command {
	/**
	 * returns if the Command is blocked or something
	 * @param args the Command-Arguments
	 * @param event The {@link MessageReceivedEvent} of the incoming {@link Message}
	 * @return true if Command should be executed, else false
	 */
	public default boolean allowExecute(String[] args, MessageReceivedEvent event) {
		return true;
	}
	/**
	 * The Execution of the Command
	 * @param args the Command-Arguments
	 * @param event The {@link MessageReceivedEvent} of the incoming {@link Message}
	 */
	public void action(String[] args, MessageReceivedEvent event);
	/**
	 * after Command execution
	 * @param success has the command been executed?
	 * @param event The {@link MessageReceivedEvent} of the incoming {@link Message}
	 */
	public default void executed(boolean success, MessageReceivedEvent event) {
		
	}
	/**
	 * help for the Command<br>
	 * @return help String
	 */
	public String help();
}
