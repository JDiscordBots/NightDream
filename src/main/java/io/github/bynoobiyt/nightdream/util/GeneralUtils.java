package io.github.bynoobiyt.nightdream.util;

import java.util.Random;

public class GeneralUtils {
	private static final Random rand=new Random();
	
	private GeneralUtils(){
		//prevent instantiation
	}
	public static String getJSONString(String json,String query) {
		String str="\""+query+"\":\"";
		if(json.indexOf(str)<0) {
			return "?";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf('\"', startIndex));
	}
	public static String getMultipleJSONStrings(String json,String query) {
		String str="\""+query+"\":[\"";
		if(json.indexOf(str)<0) {
			return "undefined";
		}
		int startIndex=json.indexOf(str)+str.length();
		return json.substring(startIndex,json.indexOf("\"]", startIndex)).replace("\"", "").replace(",", ", ");
	}
	public static int getRandInt() {
		return rand.nextInt();
	}
	public static int getRandInt(int from, int to) {
		if(from>to) {
			from^=to;
			to^=from;
			from^=to;
		}
		return rand.nextInt(to-from)+from;
	}
	public static int getRandInt(int bound) {
		return rand.nextInt(bound);
	}
	public static Random getRand() {
		return rand;
	}
}
