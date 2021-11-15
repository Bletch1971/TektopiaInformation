package bletch.tektopiainformation.utils;

import bletch.common.utils.LoggerBase;
import bletch.tektopiainformation.core.ModConfig;

public class LoggerUtils extends LoggerBase {

	public static void debug(String message, Boolean checkConfig) {
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		debug(message);
	}
	
	public static void info(String message, Boolean checkConfig) {
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		info(message);
	}

}
