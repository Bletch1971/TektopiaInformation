package bletch.tektopiainformation.utils;

import bletch.common.utils.LoggerBase;
import bletch.tektopiainformation.core.ModConfig;

public class LoggerUtils extends LoggerBase {

	public static LoggerUtils instance = new LoggerUtils();
	
	@Override
	public void debug(String message, Boolean checkConfig) {
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		debug(message);
	}
	
	@Override
	public void info(String message, Boolean checkConfig) {
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		info(message);
	}

}
