package bletch.tektopiainformation.tooltips;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.core.ModDetails;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class TektopiaItemTooltip {

	public static final String KEY_SUFFIX = ".tooltip";
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onItemTooltip(ItemTooltipEvent event) {
    	if (ModConfig.tooltips.restrictToAdvancedTooltips && !event.getFlags().isAdvanced()) {
    		return;
    	}
    	
    	List<String> tooltip = event.getToolTip();
    	if (tooltip == null) {
    		return;
    	}
    	
    	ItemStack itemStack = event.getItemStack();
		Item item = itemStack.getItem();

		ResourceLocation registryName = item.getRegistryName();
    	if (registryName == null) {
    		return;
    	}
    	
    	String resourceDomain = registryName.getResourceDomain();
    	if (!(ModDetails.MOD_ID_TEKTOPIA.equalsIgnoreCase(resourceDomain))) {
    		return;
    	}
    	
		String translateKey = itemStack.getUnlocalizedName() + KEY_SUFFIX;
		if (ModConfig.debug.enableDebug && ModConfig.debug.showTooltipTranslationKey) {
			tooltip.add(TextUtils.translate("gui.translationkey") + " " + translateKey);	
		}
		
		List<String> value = TextUtils.translateMulti(translateKey);
		if (value != null && value.size() > 0) { 
			
			if (ModConfig.tooltips.useShiftKey) {
				if (!GuiScreen.isShiftKeyDown()) {
					value = null;
					if (ModConfig.tooltips.showShiftKeyInfo) {
						value = TextUtils.translateMulti(TextUtils.KEY_SHIFTINFO);
					}
				}
			}
	
			if (value != null && value.size() > 0) { 
				tooltip.addAll(value);	
			}
		}		
    }
    
}
