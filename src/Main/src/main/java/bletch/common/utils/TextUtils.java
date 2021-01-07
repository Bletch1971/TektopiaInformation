package bletch.common.utils;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class TextUtils {
	public static final String SEPARATOR_COLON = " : ";
	public static final String SEPARATOR_DASH = " - ";
	public static final String SEPARATOR_FSLASH = "/";
	public static final String SEPARATOR_TIMES = "x ";
	public static final String INDENT = "  ";
	
	public static final String SYMBOL_GREENTICK = TextFormatting.GREEN+"\u2714";
	public static final String SYMBOL_REDCROSS = TextFormatting.RED+"\u2718";
	public static final String SYMBOL_UNKNOWN = TextFormatting.GOLD+"\u003F";
	
	public static final String SYMBOL_FEMALE = TextFormatting.LIGHT_PURPLE+"\u2640";
	public static final String SYMBOL_MALE = TextFormatting.BLUE+"\u2642";
	
	public static final String KEY_HELP = "gui.help";
	public static final String KEY_INFO = "gui.info";
	public static final String KEY_CONTROLHELP = "gui.showcontrolhelp";
	public static final String KEY_CONTROLINFO = "gui.showcontrolinfo";
	public static final String KEY_SHIFTHELP = "gui.showshifthelp";
	public static final String KEY_SHIFTINFO = "gui.showshiftinfo";
    
    public static boolean canTranslate(String key) {
        return I18n.canTranslate(key);
    }
	
	public static String translate(String translateKey) {
		if (StringUtils.isNullOrWhitespace(translateKey) || !canTranslate(translateKey)) {
			return StringUtils.EMPTY;
		}
		
		String value = I18n.translateToLocal(translateKey);
		if (StringUtils.isNullOrWhitespace(value) || value.equalsIgnoreCase(translateKey)) {
			return StringUtils.EMPTY;
		}
		
		return value;
	}
	
	public static List<String> translateMulti(String translateKey) {
		if (StringUtils.isNullOrWhitespace(translateKey) || !canTranslate(translateKey)) {
			return null;
		}
		
		String value = I18n.translateToLocal(translateKey);
		if (StringUtils.isNullOrWhitespace(value) || value.equalsIgnoreCase(translateKey)) {
			return null;
		}
		
		String[] values = value.split("\\\\n");
		return Arrays.asList(values);
	}

	public static String getClientLocale() {
		return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}
}
