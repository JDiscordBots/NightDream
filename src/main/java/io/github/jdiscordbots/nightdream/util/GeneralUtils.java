package io.github.jdiscordbots.nightdream.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONObject;

/**
 * some utilities that are not specifically for Discord
 * @author Daniel Schmid
 *
 */
public class GeneralUtils {
	private static final Random rand = new Random();
	
	private GeneralUtils(){
		//prevent instantiation
	}
	/**
	 * calculates a random integer
	 * @return the random integer
	 */
	public static int getRandInt() {
		return rand.nextInt();
	}
	/**
	 * calculates a random integer<br>
	 * from and to can be swapped.
	 * @param from the min value(inclusive) of the int
	 * @param to the max value(exclusive) of the int
	 * @return the random integer
	 */
	public static int getRandInt(int from, int to) {
		if(from>to) {
			from^=to;
			to^=from;
			from^=to;
		}
		return rand.nextInt(to-from)+from;
	}
	/**
	 * calculates a random integer
	 * @param bound the max value(exclusive) of the int
	 * @return the random integer
	 */
	public static int getRandInt(int bound) {
		return rand.nextInt(bound);
	}
	/**
	 * gets the {@link Random} object used for randomness calculation
	 * @return the {@link Random} object
	 */
	public static Random getRand() {
		return rand;
	}
	public static JSONObject getJSONFromURL(String url) {
		try(BufferedReader reader=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return new JSONObject(reader.lines().collect(Collectors.joining()));
		} catch (IOException e) {
			return null;
		}
		
	}
}
