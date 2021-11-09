package bletch.tektopiainformation.waila;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.utils.LoggerUtils;
import bletch.tektopiainformation.utils.TektopiaUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public class TektopiaBlockWaila implements IWailaDataProvider {

	public static final String KEY_SUFFIX_DESCRIPTION = ".description";
	public static final String KEY_SUFFIX_TOOLTIP = ".tooltip";
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if (ModConfig.waila.useSneaking && !accessor.getPlayer().isSneaking()) {
			return currentTip;
		}
		
		if (ModConfig.waila.blocks.showBlockTooltip) {
			String translateKey_tooltip = itemStack.getUnlocalizedName() + KEY_SUFFIX_TOOLTIP;
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaBlockTranslationKey) {
				currentTip.add(TextUtils.translate("gui.translationkey") + " " + translateKey_tooltip);
			}
			
			List<String> value = TextUtils.translateMulti(translateKey_tooltip);
			if (value != null && value.size() > 0) {
				currentTip.addAll(value);
			}
		}
		
		if (ModConfig.waila.blocks.showBlockInformation) {
			String translateKey_description = itemStack.getUnlocalizedName() + KEY_SUFFIX_DESCRIPTION;
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaBlockTranslationKey) {
				currentTip.add(TextUtils.translate("gui.translationkey") + " " + translateKey_description);
			}
			
			List<String> value = TextUtils.translateMulti(translateKey_description);
			if (value != null && value.size() > 0) {
				currentTip.addAll(value);
			}
		}
    	
		return currentTip;
	}
	
	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tag, World world, BlockPos position) {
		tileEntity.writeToNBT(tag);

		return tag;
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		TektopiaBlockWaila dataProvider = new TektopiaBlockWaila();
		ArrayList<String> processed = new ArrayList<>();
		
		ArrayList<Class<?>> tektopiaClasses = new ArrayList<>();
		tektopiaClasses.addAll(TektopiaUtils.getTektopiaBlockClasses());
		
		// remove any blocks that are inherited from other tektopia mod blocks
		for (int i = tektopiaClasses.size() - 1; i >= 0; i--) {
			if (tektopiaClasses.contains(tektopiaClasses.get(i).getSuperclass())) {
				tektopiaClasses.remove(i);
			}
		}
		
		for (Class<?> tektopiaClass : tektopiaClasses) {
        	String key = tektopiaClass.getTypeName();

        	if (processed.contains(key)) {
        		continue;
        	}
        	processed.add(key);
        	
        	registrar.registerNBTProvider(dataProvider, tektopiaClass);
			registrar.registerBodyProvider(dataProvider, tektopiaClass);
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaBlocksRegistered) {
				LoggerUtils.writeLine("Registered WAILA information for block " + key, true);
			} 
		}	
	}

}
