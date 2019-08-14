package io.github.bynoobiyt.nightdream.util;

import java.util.Random;

/**
 * some utilities that are not specifically for Discord
 * @author Daniel Schmid
 *
 */
public class GeneralUtils {
	private static final Random rand=new Random();
	
	private GeneralUtils(){
		//prevent instantiation
	}
	/**
	 * gets the string value of the first occurrence of a key in a json String
	 * @param json the JSON String
	 * @param query the key to look for
	 * @return the value of the first occurrence
	 */
	public static String getJSONString(String json,String query) {
		String str="\""+query+"\":\"";
		if(json.indexOf(str)<0) {
			return "?";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf('\"', startIndex));
	}
	/**
	 * gets the string array value of the first occurrence of a key in a json String
	 * @param json the JSON String
	 * @param query the key to look for
	 * @return the value of the first occurrence
	 */
	public static String getMultipleJSONStrings(String json,String query) {
		String str="\""+query+"\":[\"";
		if(json.indexOf(str)<0) {
			return "undefined";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf("\"]", startIndex)).replace("\"", "").replace(",", ", ");
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
}
