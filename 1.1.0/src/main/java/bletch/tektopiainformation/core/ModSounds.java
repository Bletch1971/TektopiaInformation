package bletch.tektopiainformation.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModDetails.MOD_ID)
public class ModSounds {
	
	public static SoundEvent BOOK_PAGE_TURN;
	
	public static void initialise() {
		System.out.println("[" + ModDetails.MOD_NAME + "] Registering Blocks");
		
		registerSounds();
		
		System.out.println("[" + ModDetails.MOD_NAME + "] Done Registering Blocks");
	}
	
	private static void registerSounds() {
		BOOK_PAGE_TURN = createSoundEvent("book_page_turn");
	}
	
    private static SoundEvent createSoundEvent(String soundName) {
        ResourceLocation soundID = new ResourceLocation(ModDetails.MOD_ID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
    
}
