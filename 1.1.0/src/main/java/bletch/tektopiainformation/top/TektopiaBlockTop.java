package bletch.tektopiainformation.top;

import java.util.List;

import javax.annotation.Nullable;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.core.ModDetails;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TektopiaBlockTop {
	
	public static class getTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static final String KEY_SUFFIX_DESCRIPTION = ".description";
		public static final String KEY_SUFFIX_TOOLTIP = ".tooltip";
		
		public static ITheOneProbe probe;

		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			
			probe.registerProvider(new IProbeInfoProvider() {

				@Override
				public String getID() {
					return ModDetails.MOD_ID + ":default";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
					Block block = state.getBlock();
					
					if (block.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA)) {
						TektopiaBlockTop.getTheOneProbe.addProbeInfo(mode, probeInfo, player, world, state, data);
					}	
				}

			});
			
			probe.registerBlockDisplayOverride((mode, probeInfo, player, world, state, data) -> {
				Block block = state.getBlock();
				
				if (block.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA)) {
					return TektopiaBlockTop.getTheOneProbe.overrideStandardInfo(mode, probeInfo, player, world, state, data);
				}
				
				return false;
			});
			
			return null;
		}
		
		private static boolean addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {

			if (ModConfig.top.useSneaking && mode == ProbeMode.NORMAL) {
				return false;
			}
						
			TileEntity tileEntity = world.getTileEntity(data.getPos());	
			ItemStack itemStack = data.getPickBlock();
			if (!itemStack.getItem().getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA)) {
				if (tileEntity != null) {
					itemStack = new ItemStack(state.getBlock(), 1, tileEntity.getBlockMetadata());
				} else {
					itemStack = new ItemStack(state.getBlock());
				}
			}

			if (ModConfig.top.blocks.showBlockTooltip) {
				String translateKey_tooltip = itemStack.getUnlocalizedName() + KEY_SUFFIX_TOOLTIP;
				
				if (ModConfig.debug.enableDebug && ModConfig.debug.showTopBlockTranslationKey) {
					probeInfo.text(TextUtils.translate("gui.translationkey") + " " + translateKey_tooltip);
				}

				List<String> value = TextUtils.translateMulti(translateKey_tooltip);
				if (value != null && value.size() > 0) {
					value.forEach(v -> probeInfo.text(v));
				}
			}

			if (ModConfig.top.blocks.showBlockInformation) {
				String translateKey_description = itemStack.getUnlocalizedName() + KEY_SUFFIX_DESCRIPTION;
				
				if (ModConfig.debug.enableDebug && ModConfig.debug.showTopBlockTranslationKey) {
					probeInfo.text(TextUtils.translate("gui.translationkey") + " " + translateKey_description);
				}
				
				List<String> value = TextUtils.translateMulti(translateKey_description);
				if (value != null && value.size() > 0) {
					value.forEach(v -> probeInfo.text(v));
				}
			}
			
			return true;
		}
		
		private static boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
			return false;
		}
		
	}
	
}
