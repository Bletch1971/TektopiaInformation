package bletch.tektopiainformation.top;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.core.ModDetails;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class TektopiaEntityTop {
	
	public static class getTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static ITheOneProbe probe;

		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			
			probe.registerEntityProvider(new IProbeInfoEntityProvider() {
				
				@Override
				public String getID() {
					return ModDetails.MOD_ID + ":default";
				}

				@Override
				public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
					
					if (entity instanceof EntityVillagerTek) {
						TektopiaEntityTop.getTheOneProbe.addEntityVillagerTekInfo(mode, probeInfo, player, world, (EntityVillagerTek)entity, data);
					}	
					
				}
				
			});
			
			return null;
		}

		private static boolean addEntityVillagerTekInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, EntityVillagerTek entity, IProbeHitEntityData data) {

			if (ModConfig.top.useSneaking && mode == ProbeMode.NORMAL) {
				return false;
			}
			
			EntityVillagerTek villager = entity;
			String output = "";
			
			if (villager != null) {
				boolean showVillageDetails = false;
				
				if (villager instanceof EntityArchitect) {
					showVillageDetails = true;
				}
				else if (villager instanceof EntityTradesman) {
					showVillageDetails = true;
				}
				else if (villager.getProfessionType() != null) {
					String profession = "";
					int professionSkillLevel = 0;
					output = "";
					
					// Profession
					ProfessionType professionType = villager.getProfessionType();
					switch (professionType) {
					case CHILD:
					case NITWIT:
						profession = TextUtils.translate("entity." + professionType.name + ".name");
						output = profession;
						probeInfo.text(TextFormatting.DARK_AQUA + output);
						
						showVillageDetails = true;
						break;	
					case NOMAD:
						profession = TextUtils.translate("entity." + professionType.name + ".name");
						output = profession;
						probeInfo.text(TextFormatting.DARK_AQUA + output);
						break;
					case GUARD:
						if (villager instanceof EntityGuard && ((EntityGuard)villager).isCaptain()) {
							profession = TextUtils.translate("entity." + ProfessionType.CAPTAIN.name + ".name");
						} else {
							profession = TextUtils.translate("entity." + professionType.name + ".name");
						}
						professionSkillLevel = villager.getSkill(professionType);
						
						output = profession;
						if (professionSkillLevel > 0) {
							if (villager.getBlessed() > 0) {
								int professionBaseSkillLevel = villager.getBaseSkill(professionType);
								
								output += " - level " + TextFormatting.GREEN + professionSkillLevel + TextFormatting.WHITE + " (" + professionBaseSkillLevel + ")";
							} else {
								output += " - level " + TextFormatting.WHITE + professionSkillLevel;
							}
						}
						probeInfo.text(TextFormatting.DARK_AQUA + output);	
						
						showVillageDetails = true;
						break;
					default:
						profession = TextUtils.translate("entity." + professionType.name + ".name");
						professionSkillLevel = villager.getSkill(professionType);
						
						output = profession;
						if (professionSkillLevel > 0) {
							if (villager.getBlessed() > 0) {
								int professionBaseSkillLevel = villager.getBaseSkill(professionType);
								
								output += " - level " + TextFormatting.GREEN + professionSkillLevel + TextFormatting.WHITE + " (" + professionBaseSkillLevel + ")";
							} else {
								output += " - level " + TextFormatting.WHITE + professionSkillLevel;
							}
						}
						probeInfo.text(TextFormatting.DARK_AQUA + output);
						
						showVillageDetails = true;
						break;
					}
					
					// Days Alive
					int daysAlive = villager.getDaysAlive();
					if (daysAlive > 0) {
						output = TextUtils.translate("gui.villager.daysalive") + " " + TextFormatting.WHITE + daysAlive;
						probeInfo.text(TextFormatting.DARK_AQUA + output);
					}
					
					// Health
					float health = villager.getHealth();
					output = TextUtils.translate("gui.villager.health") + " " + TextFormatting.WHITE + health;
					probeInfo.text(TextFormatting.DARK_AQUA + output);
					
					// Hunger
					int hunger = villager.getHunger();
					output = TextUtils.translate("gui.villager.hunger") + " " + TextFormatting.WHITE + hunger;
					probeInfo.text(TextFormatting.DARK_AQUA + output);
					
					// Happy
					int happy = villager.getHappy();
					output = TextUtils.translate("gui.villager.happy") + " " + TextFormatting.WHITE + happy;
					probeInfo.text(TextFormatting.DARK_AQUA + output);
					
					// Intelligence
					int intelligence = villager.getIntelligence();
					output = TextUtils.translate("gui.villager.intelligence") + " " + TextFormatting.WHITE + intelligence;
					probeInfo.text(TextFormatting.DARK_AQUA + output);
					
					// Blessed
					int blessed = villager.getBlessed();
					if (blessed > 0) {
						output = TextUtils.translate("gui.villager.blessedlevels") + " " + TextFormatting.WHITE + blessed;
						probeInfo.text(TextFormatting.DARK_AQUA + output);
					}					
				}
				
				if (showVillageDetails) {
					// Village
					Village village = villager.getVillage();
					if (village != null) {
						
						String villageName = village.getName();
						output = TextUtils.translate("gui.villager.villagename") + " " + TextFormatting.WHITE + villageName;
						probeInfo.text(TextFormatting.DARK_AQUA + output);
						
						// has bed
						boolean hasBed = villager.getBedPos() != null;
						output = TextUtils.translate("gui.villager.hasbed") + " " + TextFormatting.WHITE + (hasBed ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
						probeInfo.text(TextFormatting.DARK_AQUA + output);					
					}					
				}
				
				return true;
			}

			return false;
		}
	}
}
