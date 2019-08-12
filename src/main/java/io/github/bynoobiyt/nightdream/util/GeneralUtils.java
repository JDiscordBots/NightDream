package io.github.bynoobiyt.nightdream.util;

public class GeneralUtils {
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
}
