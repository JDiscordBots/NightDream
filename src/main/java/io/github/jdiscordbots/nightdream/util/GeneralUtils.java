/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: GeneralUtils.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
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
		return getJSONFromURL(url,StandardCharsets.UTF_8);
    }
	public static JSONObject getJSONFromURL(String url,Charset charset) {
		try(BufferedReader reader=new BufferedReader(new InputStreamReader(openStreamWithRandomUserAgent(url),charset))){
			return new JSONObject(reader.lines().collect(Collectors.joining()));
		} catch (IOException e) {
			return null;
		}
    }
	public static JSONObject getJSONFromURLWithHeaders(String url,Map<String, String> headers) {
		return getJSONFromURLWithHeaders(url,headers,StandardCharsets.UTF_8);
    }
	public static JSONObject getJSONFromURLWithHeaders(String url,Map<String, String> headers,Charset charset) {
		try(BufferedReader reader=new BufferedReader(new InputStreamReader(openStreamWithHeaders(url,headers),charset))){
			return new JSONObject(reader.lines().collect(Collectors.joining()));
		} catch (IOException e) {
			return null;
		}
	}
    public static InputStream openStreamWithRandomUserAgent(String url)throws IOException{
    	Map<String, String> headers=new HashMap<>();
    	headers.put("User-Agent", UUID.randomUUID().toString());
    	return openStreamWithHeaders(url,headers);
    }
    public static InputStream openStreamWithHeaders(String url,Map<String, String> headers)throws IOException{
        URLConnection con=new URL(url).openConnection();
        headers.forEach(con::setRequestProperty);
        return con.getInputStream();
    }
}
