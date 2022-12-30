package bletch.tektopiainformation.core;

import java.io.File;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.tektopiainformation.TektopiaInformation;
import bletch.tektopiainformation.network.handlers.VillageMessageHandlerOnClient;
import bletch.tektopiainformation.network.messages.VillageMessageToClient;
import bletch.tektopiainformation.tooltips.TektopiaItemTooltip;
import bletch.tektopiainformation.top.TektopiaBlockTop;
import bletch.tektopiainformation.top.TektopiaEntityTop;
import bletch.tektopiainformation.utils.LoggerUtils;
import bletch.tektopiainformation.waila.TektopiaBlockWaila;
import bletch.tektopiainformation.waila.TektopiaEntityWaila;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@ParametersAreNonnullByDefault
public class ModClientProxy extends ModCommonProxy {

	@Override
	public boolean isRemote() {
		return true;
	}

	@Override
	public File getMinecraftDirectory() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public void preInitialize(FMLPreInitializationEvent event) {
		super.preInitialize(event);
		
		// Register client message handler in clientOnly proxy, and a dummy server message handler in common proxy.
		TektopiaInformation.NETWORK.registerMessage(VillageMessageHandlerOnClient.class, VillageMessageToClient.class, ModDetails.MESSAGE_ID_VILLAGE, Side.CLIENT);
	}
	
	@Override
	public void registerGui() {
		super.registerGui();
	}
	
	@Override
	public void registerSounds() {
		super.registerSounds();
	}
	
	@Override
	public void registerTheOneProbe() {
		super.registerTheOneProbe();
		
		if (Loader.isModLoaded(ModDetails.MOD_ID_TOP) && ModConfig.top.enableTopIntegration) {
			LoggerUtils.instance.info("Registering blocks with The One Probe");
			FMLInterModComms.sendFunctionMessage(ModDetails.MOD_ID_TOP, "getTheOneProbe", TektopiaBlockTop.class.getTypeName() + "$getTheOneProbe");
			LoggerUtils.instance.info("Registered blocks with The One Probe");
			
			LoggerUtils.instance.info("Registering entities with The One Probe");
			FMLInterModComms.sendFunctionMessage(ModDetails.MOD_ID_TOP, "getTheOneProbe", TektopiaEntityTop.class.getTypeName() + "$getTheOneProbe");
			LoggerUtils.instance.info("Registered entities with The One Probe");
		}
	}
	
	@Override
	public void registerTooltips() {
		super.registerTooltips();
		
    	if (ModConfig.tooltips.enableTooltipIntegration) {
    		LoggerUtils.instance.info("Registering Item Tooltip");
			MinecraftForge.EVENT_BUS.register(new TektopiaItemTooltip());
			LoggerUtils.instance.info("Registered Item Tooltip");
    	}
	}
	
	public void registerWaila() {
		super.registerWaila();
		
		if (Loader.isModLoaded(ModDetails.MOD_ID_WAILA) && ModConfig.waila.enableWailaIntegration) {
			LoggerUtils.instance.info("Registering blocks with Waila");
			FMLInterModComms.sendMessage(ModDetails.MOD_ID_WAILA, "register", TektopiaBlockWaila.class.getTypeName() + ".callbackRegister");
			LoggerUtils.instance.info("Registered blocks with Waila");
			
			LoggerUtils.instance.info("Registering entities with Waila");
			FMLInterModComms.sendMessage(ModDetails.MOD_ID_WAILA, "register", TektopiaEntityWaila.class.getTypeName() + ".callbackRegister");
			LoggerUtils.instance.info("Registered entities with Waila");
		}
	}
	
}
