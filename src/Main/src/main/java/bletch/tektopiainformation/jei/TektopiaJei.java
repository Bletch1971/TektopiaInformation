package bletch.tektopiainformation.jei;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.core.ModDetails;
import bletch.tektopiainformation.utils.LoggerUtils;
import bletch.tektopiainformation.utils.TektopiaUtils;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@JEIPlugin
@ParametersAreNonnullByDefault
public class TektopiaJei implements IModPlugin {
	
	private static final String KEY_SUFFIX = ".information";

    private static IJeiHelpers jeiHelpers;
    private static IJeiRuntime jeiRuntime;

    private static List<BlankRecipeCategory<?>> categories = new ArrayList<BlankRecipeCategory<?>>();
    
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    	TektopiaJei.jeiRuntime = jeiRuntime;
    }
    
    @Override
    public void register(IModRegistry registry) {

    	if (!ModConfig.jei.enableJeiIntegration) {
			return;
		}
    	
    	if (ModConfig.jei.showJeiInformationTab) {
    		try {
    			registerTektopiaIngredientInfo(registry);
    		}
    		catch (Exception ex) {
    			LoggerUtils.error(ex.getMessage());
    		}    		
    	}
    	
	}
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
    	TektopiaJei.jeiHelpers = registry.getJeiHelpers();
    	
		if (!ModConfig.jei.enableJeiIntegration) {
			return;
		}
    	
    	categories.forEach(c -> registry.addRecipeCategories(c));
	}    

    public static BlankRecipeCategory<?> getCategory(Class<?> categoryClass) {
    	return categories.stream()
    			.filter(categoryClass::isInstance)
    			.findFirst().orElse(null);
    }
    
    public static IGuiHelper getGuiHelper() {
        return TektopiaJei.jeiHelpers.getGuiHelper();
    }
    
    public static IJeiHelpers getJeiHelper() {
        return TektopiaJei.jeiHelpers;
    }

    public static IJeiRuntime getJeiRuntime() {
        return TektopiaJei.jeiRuntime;
    }

	public static String getJeiUid(String category) {
		return ModDetails.MOD_ID + ":" + category;
	}
	
	private static void registerTektopiaIngredientInfo(IModRegistry registry) {
		ArrayList<String> processed = new ArrayList<String>();
		
		LoggerUtils.info("Registering item/block information with JEI");
		
		ArrayList<ItemStack> tektopiaItemStacks = new ArrayList<ItemStack>();
		tektopiaItemStacks.addAll(TektopiaUtils.getTektopiaBlockStacks());
		tektopiaItemStacks.addAll(TektopiaUtils.getTektopiaItemStacks());
		
		ArrayList<String> missingInformation = new ArrayList<String>();
		ArrayList<String> registeredInformation = new ArrayList<String>();		
		int count = 0;
		
    	for (ItemStack tektopiaItemStack : tektopiaItemStacks) {
        
			NonNullList<ItemStack> itemStackList = NonNullList.create();

			if (tektopiaItemStack.getHasSubtypes()) {
				Item item = tektopiaItemStack.getItem();
				item.getSubItems(item.getCreativeTab(), itemStackList);
    		} else {
    			itemStackList.add(tektopiaItemStack);
    		}

			for (ItemStack itemStack : itemStackList) {
				String key = itemStack.getUnlocalizedName() + KEY_SUFFIX;
				
	        	if (processed.contains(key)) {
	        		continue;
	        	}
	        	processed.add(key);
	        	
	        	// check if the item belongs to the Tektopia domain
	        	if (!itemStack.getItem().getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA)) {
	        		continue;
	        	}
	        	
				if (TextUtils.canTranslate(key) || ModConfig.debug.enableDebug && ModConfig.debug.registerJeiMissingInformation) {
					
					try {
						registry.addIngredientInfo(itemStack, VanillaTypes.ITEM, key);
						count++;
						
						if (ModConfig.debug.enableDebug && ModConfig.debug.showJeiRegisteredInformation) {
							registeredInformation.add("Registered JEI information for " + itemStack.getDisplayName() + "; key: " + key);
						} 
					}
		    		catch (Exception e) {
		    			if (ModConfig.debug.enableDebug) {
		    				LoggerUtils.writeLine("Error registering JEI information for " + itemStack.getDisplayName() + "; key: " + key, true);
		    			}
		    		}
					
				} else {

					if (ModConfig.debug.enableDebug && ModConfig.debug.showJeiMissingInformation) {
						missingInformation.add("Missing JEI information for " + itemStack.getDisplayName() + "; key: " + key);
        			}
					
				}
			}
		}
    	
    	if (ModConfig.debug.enableDebug && registeredInformation != null && registeredInformation.size() > 0) {
    		registeredInformation.sort((i1, i2) -> i1.compareTo(i2));
    		LoggerUtils.writeLines(registeredInformation, true);
    	}
    	
    	if (ModConfig.debug.enableDebug && missingInformation != null && missingInformation.size() > 0) {
    		missingInformation.sort((i1, i2) -> i1.compareTo(i2));
    		LoggerUtils.writeLines(missingInformation, true);
    	}
    	
    	LoggerUtils.info("Registered item/block information with JEI - count: " + count);
	}

}
