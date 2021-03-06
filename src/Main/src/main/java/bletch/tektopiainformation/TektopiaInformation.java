package bletch.tektopiainformation;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.tektopiainformation.core.ModCommonProxy;
import bletch.tektopiainformation.core.ModDetails;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid=ModDetails.MOD_ID, name=ModDetails.MOD_NAME, version=ModDetails.MOD_VERSION, dependencies=ModDetails.MOD_DEPENDENCIES, acceptableRemoteVersions="*", acceptedMinecraftVersions="[1.12.2]", updateJSON=ModDetails.MOD_UPDATE_URL)
@ParametersAreNonnullByDefault
public class TektopiaInformation {
	
	public static final SimpleNetworkWrapper NETWORK;

	@Instance(ModDetails.MOD_ID)
	public static TektopiaInformation instance;

	@SidedProxy(clientSide = ModDetails.MOD_CLIENT_PROXY_CLASS, serverSide = ModDetails.MOD_SERVER_PROXY_CLASS)
	public static ModCommonProxy proxy;
	
	@Mod.EventHandler
	public void preInitialize(FMLPreInitializationEvent event) {
		instance = this;
		
		proxy.preInitialize(event);
		proxy.resetDebug();
	}
	  
	@Mod.EventHandler
	public void initialize(FMLInitializationEvent event) {
		proxy.registerTooltips();
		proxy.registerWaila();
		proxy.registerTheOneProbe();
		proxy.registerGui();
		proxy.registerSounds();
	}
	  
	@Mod.EventHandler
	public void postInitialize(FMLPostInitializationEvent event) {

	}
	
	static {
		NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ModDetails.MOD_ID);
	}

}
