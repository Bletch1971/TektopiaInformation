package bletch.tektopiainformation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.core.ModDetails;

public class LoggerUtils {
	
	private static final Logger MOD_LOGGER = LogManager.getLogger(ModDetails.MOD_NAME);
	
	public static void debug(String message) {
		if (message == null)
			return;
		
		MOD_LOGGER.debug(message);
	}
	
	public static void debug(String message, Boolean checkConfig) {
		if (message == null)
			return;
		
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		MOD_LOGGER.debug(message);
	}
	
	public static void error(String message) {
		if (message == null)
			return;
		
		MOD_LOGGER.error(message);
	}
	
	public static void fatal(String message) {
		if (message == null)
			return;
		
		MOD_LOGGER.fatal(message);
	}
	
	public static void info(String message) {
		if (message == null)
			return;
		
		MOD_LOGGER.info(message);
	}
	
	public static void info(String message, Boolean checkConfig) {
		if (message == null)
			return;
		
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		MOD_LOGGER.info(message);
	}
	
	public static void trace(String message) {
		if (message == null)
			return;
		
		MOD_LOGGER.trace(message);
	}
	
	public static void warn(String message) {
		if (message == null)
			return;
		
		MOD_LOGGER.warn(message);
	}

}
