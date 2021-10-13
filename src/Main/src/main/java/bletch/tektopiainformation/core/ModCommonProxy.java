package bletch.tektopiainformation.core;

import java.io.File;
import javax.annotation.ParametersAreNonnullByDefault;

import bletch.tektopiainformation.TektopiaInformation;
import bletch.tektopiainformation.network.handlers.PlayerInteractHandler;
import bletch.tektopiainformation.network.handlers.VillageMessageHandlerOnServer;
import bletch.tektopiainformation.network.messages.VillageMessageToClient;
import bletch.tektopiainformation.utils.DebugUtils;
import bletch.tektopiainformation.utils.LoggerUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@ParametersAreNonnullByDefault
public class ModCommonProxy {
	
	public boolean isRemote() {
		return false;
	}

	public File getMinecraftDirectory() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");
	}

	public void preInitialize(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ModConfig());
		
		// Register client message handler in clientOnly proxy, and a dummy server message handler in common proxy.
		TektopiaInformation.NETWORK.registerMessage(VillageMessageHandlerOnServer.class, VillageMessageToClient.class, ModDetails.MESSAGE_ID_VILLAGE, Side.SERVER);
	}
	
	public void registerGui() {
    	if (ModConfig.gui.enableGuiIntegration) {
    		LoggerUtils.info("Registering Gui");
			MinecraftForge.EVENT_BUS.register(new PlayerInteractHandler());
			LoggerUtils.info("Registered Gui");
		}
	}
	
	public void registerSounds() {
		ModSounds.initialise();
	}
	
	public void registerTheOneProbe() {
	}
	
	public void registerTooltips() {
	}
	
	public void registerWaila() {
	}
	
	public void resetDebug() {
		if (ModConfig.debug.enableDebug) {
			DebugUtils.resetDebug();
		}		
	}
	
}
