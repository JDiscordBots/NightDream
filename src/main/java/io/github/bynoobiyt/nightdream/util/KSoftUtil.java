package io.github.bynoobiyt.nightdream.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explodingbush.ksoftapi.KSoftAPI;
import net.explodingbush.ksoftapi.entities.TaggedImage;
import net.explodingbush.ksoftapi.enums.ImageTag;

public class KSoftUtil {
	
	private static KSoftAPI api;
	
	private static final Logger LOG=LoggerFactory.getLogger(KSoftUtil.class);
	
	static {
		String token=BotData.getKSoftToken();
		if("".equals(token)) {
			LOG.warn("no ksoft token defined");
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
	public static TaggedImage getImage(ImageTag tag) {
		if(api==null) {
			return null;
		}
		return api.getTaggedImage(tag).allowNsfw(false).execute();
	}

}
