package bletch.common.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.FontRenderer;

public class StringUtils {

	public static final String EMPTY = "";
	
	public static Boolean isNullOrWhitespace(@Nullable String value) {
		return (value == null || value.trim().length() == 0);
	}
	
	public static List<String> split(String text, int displayWidth, FontRenderer fontRenderer) {
		if (isNullOrWhitespace(text) || fontRenderer == null) {
			return new ArrayList<String>();
		}
		
		List<String> result = new ArrayList<String>();
		
		int outputWidth = fontRenderer.getStringWidth(text);
		
		// check if the text is longer than half the display width
		if (outputWidth > displayWidth) {
			// break the text into parts using the whitespaces 
			String[] descriptionParts = text.split(" ");
			String delimiter = StringUtils.EMPTY;
			
			// clear the output
			String output = StringUtils.EMPTY;
			
			// cycle through each part
			for (String part : descriptionParts) {
				// check if the current output length is longer than the display width
				outputWidth = fontRenderer.getStringWidth(output + delimiter + part);
				
				if (outputWidth >= displayWidth) {
					result.add(output);
					
					// reset the output text and delimiter
					output = StringUtils.EMPTY;
					delimiter = StringUtils.EMPTY;
				}
				
				// add the part to the output
				output += delimiter + part;
				delimiter = " ";
			}       			
			
			// check if the output is empty
			if (!StringUtils.isNullOrWhitespace(output)) {
				// output is not empty, add to the tooltip
				result.add(output);
			}
		} else {
			result.add(text);
		}
		
		return result;
	}
}
