package bletch.common.utils;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ParametersAreNonnullByDefault
public class TextUtils {
	public static final String SEPARATOR_COLON = " : ";
	public static final String SEPARATOR_DASH = " - ";
	public static final String SEPARATOR_FSLASH = "/";
	public static final String SEPARATOR_TIMES = " x ";
	public static final String INDENT = "  ";

	public static final String SYMBOL_BULLET = "\u2022";
	public static final String SYMBOL_GREENTICK = TextFormatting.DARK_GREEN+"\u2714";
	public static final String SYMBOL_GREENCROSS = TextFormatting.DARK_GREEN+"\u2718";
	public static final String SYMBOL_REDCROSS = TextFormatting.RED+"\u2718";
	public static final String SYMBOL_REDTICK = TextFormatting.RED+"\u2714";
	public static final String SYMBOL_UNKNOWN = TextFormatting.GOLD+"\u003F";
	
	public static final String SYMBOL_FEMALE = TextFormatting.LIGHT_PURPLE+"\u2640";
	public static final String SYMBOL_MALE = TextFormatting.BLUE+"\u2642";
	
	public static final String KEY_HELP = "gui.help";
	public static final String KEY_INFO = "gui.info";
	public static final String KEY_CONTROLHELP = "gui.showcontrolhelp";
	public static final String KEY_CONTROLINFO = "gui.showcontrolinfo";
	public static final String KEY_SHIFTHELP = "gui.showshifthelp";
	public static final String KEY_SHIFTINFO = "gui.showshiftinfo";
    
	public static boolean canTranslate(String translateKey) {
		if (StringUtils.isNullOrWhitespace(translateKey)) {
			return false;
		}
		
		String translate = translate(translateKey);
		return !StringUtils.isNullOrWhitespace(translate) && !translate.equalsIgnoreCase(translateKey);
	}
	
	public static String translate(String translateKey, Object... translationArgs) {
		if (StringUtils.isNullOrWhitespace(translateKey)) {
			return null;
		}
		
		ITextComponent itextcomponent = new TextComponentTranslation(translateKey, translationArgs);
		String translate = itextcomponent.getUnformattedText();
		
		return StringUtils.isNullOrWhitespace(translate) || translate.equalsIgnoreCase(translateKey) 
				? null 
				: translate;
	}
	
	public static List<String> translateMulti(String translateKey, Object... translationArgs) {
		if (StringUtils.isNullOrWhitespace(translateKey)) {
			return null;
		}
		
		String translate = translate(translateKey, translationArgs);
		if (StringUtils.isNullOrWhitespace(translate) || translate.equalsIgnoreCase(translateKey)) {
			return null;
		}
		
		String[] values = translate.split("\\\\n");
		return Arrays.asList(values);
	}
	
}
