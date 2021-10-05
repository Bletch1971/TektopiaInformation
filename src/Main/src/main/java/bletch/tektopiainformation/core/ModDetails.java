package bletch.tektopiainformation.core;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ParametersAreNonnullByDefault
public class ModDetails {
	private static final int VersionMajor = 1;
	private static final int VersionMinor = 6;
	private static final int VersionRevision = 0;

	public static final String MOD_ID = "tektopiainformation";
	public static final String MOD_NAME = "TektopiaInformation";

	public static final String MOD_VERSION = VersionMajor + "." + VersionMinor + "." + VersionRevision;
	public static final String MOD_DEPENDENCIES = "required-after:minecraft@[1.12.2];required-after:forge@[14.23.5.2768,);required-after:tektopia@[1.0.0,);after:jei;after:waila;after:theoneprobe";

	public static final String MOD_UPDATE_URL="https://raw.githubusercontent.com/Bletch1971/TektopiaInformation/master/1.0/updateforge.json";
			
	public static final String MOD_SERVER_PROXY_CLASS = "bletch.tektopiainformation.core.ModCommonProxy";
	public static final String MOD_CLIENT_PROXY_CLASS = "bletch.tektopiainformation.core.ModClientProxy";
	
	public static final Logger MOD_LOGGER = LogManager.getLogger(MOD_NAME);

	public static final String MOD_ID_MINECRAFT = "minecraft";
	public static final String MOD_ID_FORGE = "forge";
	public static final String MOD_ID_TEKTOPIA = "tektopia";
	public static final String MOD_ID_JEI = "jei";
	public static final String MOD_ID_WAILA = "waila";
	public static final String MOD_ID_TOP = "theoneprobe";
	
	public static final String PATH_DEBUG = "/debug";
	public static final String FILE_DEBUGLOG = PATH_DEBUG + "/" + MOD_ID + ".txt"; 
	
	public static final byte MESSAGE_ID_VILLAGE = 63;
}
