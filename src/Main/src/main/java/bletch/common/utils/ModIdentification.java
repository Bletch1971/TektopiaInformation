package bletch.common.utils;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.HashMap;
import java.util.Map;

public class ModIdentification {

    public static Map<String, ModContainer> containers = new HashMap<>();

    public static void init() {
        containers.put("minecraft", Loader.instance().getMinecraftModContainer());
        containers.put("forge", ForgeModContainer.getInstance());
    }

    public static String getStackModId(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return "";

        return stack.getItem().getCreatorModId(stack);
    }

    public static String getStackModName(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return "";
        
        String modId = getStackModId(stack);
        
        ModContainer container = findModContainer(modId);
        if (container == null)
        	return "";

        return container.getName();
    }

    public static String getEntityModId(Entity entity) {
    	if (entity == null)
    		return "";
    	
        EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
        if (entityEntry == null)
            return "";

        return entityEntry.getRegistryName().getResourceDomain();
    }

    public static String getEntityModName(Entity entity) {
    	if (entity == null)
    		return "";
    	
        String modId = getEntityModId(entity);
        
        ModContainer container = findModContainer(modId);
        if (container == null)
        	return "";
        
        return container.getName();
    }

    public static ModContainer findModContainer(String modId) {
        return containers.computeIfAbsent(modId, s -> {
            for (ModContainer container : Loader.instance().getModList())
                if (modId.equalsIgnoreCase(container.getModId()))
                    return container;

            return Loader.instance().getMinecraftModContainer();
        });
    }
}
