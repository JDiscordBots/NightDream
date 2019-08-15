package io.github.bynoobiyt.nightdream.util;

import net.explodingbush.ksoftapi.KSoftAPI;

public class KSoftUtil {
	
	private static KSoftAPI api;
	
	static {
		String token=BotData.getKSoftToken();
		if(token.equals("")) {
			System.err.println("no ksoft token defined");
			api=null;
		}else {
			api=new KSoftAPI(token);
		}
	}
	private KSoftUtil() {
		//prevent instantiation
	}
	
	public static KSoftAPI getApi() {
		return api;
	}

}
