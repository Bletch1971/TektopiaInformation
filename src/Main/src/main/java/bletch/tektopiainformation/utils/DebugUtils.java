package bletch.tektopiainformation.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import bletch.tektopiainformation.TektopiaInformation;
import bletch.tektopiainformation.core.ModDetails;

public class DebugUtils {
	
	public static File debugFile = new File(TektopiaInformation.proxy.getMinecraftDirectory() + ModDetails.FILE_DEBUGLOG);
	
	public static void resetDebug() {
		writeLines(Arrays.asList("Debug Log:"), false);
	}
	
	public static void writeLine(String line, Boolean append) {
		writeLines(Arrays.asList(line), append);
	}	
	
	public static void writeLines(Collection<String> lines, Boolean append) {
		try {
			FileUtils.writeLines(debugFile, lines, append);
		} 
		catch (IOException e) {
		}
	}
}
