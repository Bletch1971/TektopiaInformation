package bletch.common.jei;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public class RegistrationHandler {

	@SubscribeEvent
	public static void addRegistry(RegistryEvent.NewRegistry event) {

	}

}
