package bletch.tektopiainformation.waila;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.StringUtils;
import bletch.common.utils.TektopiaUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.utils.LoggerUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVillagerTek;

@ParametersAreNonnullByDefault
public class TektopiaEntityWaila implements IWailaEntityProvider {
	
	private static String NBT_TAG_BEDPOSITION = "bedPosition";
	private static String NBT_TAG_DAYSALIVE = "daysAlive";
	private static String NBT_TAG_HASVILLAGE = "hasVillage";
	private static String NBT_TAG_VILLAGENAME = "villageName";
	
	@Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return accessor.getEntity();
    }

	@Override
	public List<String> getWailaHead(Entity entity, List<String> currentTip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		
		if (entity instanceof EntityVillagerTek) {
			EntityVillagerTek villager = ((EntityVillagerTek)entity);

			String tooltip = TextFormatting.RESET.toString();

			// add villager name
			ITextComponent textComponent = villager.getDisplayName();
			// check if not a villager
			if (villager.getProfessionType() == null || textComponent.getFormattedText() == null) {
				textComponent = null;
			}

			if (textComponent != null) {
				String villagerName = textComponent.getFormattedText();
				tooltip += villagerName;
			} else {
				String villagerName = villager.getName();
				if (villager instanceof EntityGuard && ((EntityGuard)villager).isCaptain()) {
					String profession = TextUtils.translate("entity.captain.name");
					villagerName = profession + " " + villagerName;
				}
				tooltip += TextFormatting.WHITE + villagerName;
			}

			// add villager gender
			if (villager.isMale()) {
				tooltip += " " + TextUtils.SYMBOL_MALE;
			} else {
				tooltip += " " + TextUtils.SYMBOL_FEMALE;
			}

			if (!StringUtils.isNullOrWhitespace(tooltip) && !tooltip.equalsIgnoreCase(TextFormatting.RESET.toString())) {
				if (currentTip.size() > 0) {
					currentTip.set(0, tooltip);
				} else {
					currentTip.add(tooltip);
				}
			} else {
				if (currentTip.size() > 0) {
					currentTip.remove(0);
				}
			}
		}
		
		return currentTip;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> currentTip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {

		if (ModConfig.waila.useSneaking && !accessor.getPlayer().isSneaking()) {
			return currentTip;
		}
				
		if (!ModConfig.waila.entities.showEntityInformation) {
			return currentTip;
		}
		
		NBTTagCompound tag = accessor.getNBTData();
		String output = "";
		
		if (entity instanceof EntityVillagerTek) {
			EntityVillagerTek villager = ((EntityVillagerTek)entity);

			boolean showVillageDetails = false;

			if (entity instanceof EntityArchitect) {
				// Profession
				output = TextUtils.translate("entity.architect.name");
				currentTip.add(TextFormatting.DARK_AQUA + output);

				// Health
				float health = villager.getHealth();
				output = TextUtils.translate("gui.villager.health") + " " + TextFormatting.WHITE + health;
				currentTip.add(TextFormatting.DARK_AQUA + output);

				showVillageDetails = true;
			}
			else if (entity instanceof EntityMerchant) {
				// Profession
				output = TextUtils.translate("entity.merchant.name");
				currentTip.add(TextFormatting.DARK_AQUA + output);
			}
			else if (entity instanceof EntityTradesman) {
				// Profession
				output = TextUtils.translate("entity.tradesman.name");
				currentTip.add(TextFormatting.DARK_AQUA + output);

				// Health
				float health = villager.getHealth();
				output = TextUtils.translate("gui.villager.health") + " " + TextFormatting.WHITE + health;
				currentTip.add(TextFormatting.DARK_AQUA + output);

				showVillageDetails = true;
			}
			else if (villager.getProfessionType() != null) {
				String profession = "";
				int professionSkillLevel = 0;

				// Profession
				ProfessionType professionType = villager.getProfessionType();
				switch (professionType) {
				case CHILD:
				case NITWIT:
					profession = TextUtils.translate("entity." + professionType.name + ".name");
					output = profession;
					currentTip.add(TextFormatting.DARK_AQUA + output);

					showVillageDetails = true;
					break;
				case NOMAD:
					profession = TextUtils.translate("entity." + professionType.name + ".name");
					output = profession;
					currentTip.add(TextFormatting.DARK_AQUA + output);
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
					currentTip.add(TextFormatting.DARK_AQUA + output);

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
					currentTip.add(TextFormatting.DARK_AQUA + output);

					showVillageDetails = true;
					break;
				}

				// Days Alive
				int daysAlive = villager.getDaysAlive();
				if (daysAlive <= 0 && tag.hasKey(NBT_TAG_DAYSALIVE)) {
					daysAlive = tag.getInteger(NBT_TAG_DAYSALIVE);
				}
				if (daysAlive > 0) {
					output = TextUtils.translate("gui.villager.daysalive") + " " + TextFormatting.WHITE + daysAlive;
					currentTip.add(TextFormatting.DARK_AQUA + output);
				}

				// Health
				float health = villager.getHealth();
				output = TextUtils.translate("gui.villager.health") + " " + TextFormatting.WHITE + health;
				currentTip.add(TextFormatting.DARK_AQUA + output);

				// Hunger
				int hunger = villager.getHunger();
				output = TextUtils.translate("gui.villager.hunger") + " " + TextFormatting.WHITE + hunger;
				currentTip.add(TextFormatting.DARK_AQUA + output);

				// Happy
				int happy = villager.getHappy();
				output = TextUtils.translate("gui.villager.happy") + " " + TextFormatting.WHITE + happy;
				currentTip.add(TextFormatting.DARK_AQUA + output);

				// Intelligence
				int intelligence = villager.getIntelligence();
				output = TextUtils.translate("gui.villager.intelligence") + " " + TextFormatting.WHITE + intelligence;
				currentTip.add(TextFormatting.DARK_AQUA + output);

				// Blessed
				int blessed = villager.getBlessed();
				if (blessed > 0) {
					output = TextUtils.translate("gui.villager.blessedlevels") + " " + TextFormatting.WHITE + blessed;
					currentTip.add(TextFormatting.DARK_AQUA + output);
				}

				// Additional Professions
				boolean addProfessionCountShown = false;

				for (ProfessionType addProfessionType : TektopiaUtils.getProfessionTypes()) {
					if (addProfessionType == villager.getProfessionType()) {
						// do not include the villagers main profession
						continue;
					}

					profession = TextUtils.translate("entity." + addProfessionType.name + ".name");
					professionSkillLevel = villager.getSkill(addProfessionType);

					if (professionSkillLevel > 0) {
						if (!addProfessionCountShown) {
							output = TextUtils.translate("gui.villager.additionalprofessions");
							currentTip.add(TextFormatting.WHITE + output);

							addProfessionCountShown = true;
						}

						output = TextUtils.SYMBOL_BULLET + " " + profession + TextUtils.SEPARATOR_DASH + TextFormatting.WHITE + professionSkillLevel;
						currentTip.add(TextFormatting.DARK_AQUA + output);
					}
				}
			}

			if (showVillageDetails) {
				// Village
				Village village = villager.getVillage();

				if (village != null) {

					String villageName = village.getName();
					output = TextUtils.translate("gui.villager.villagename") + " " + TextFormatting.WHITE + villageName;
					currentTip.add(TextFormatting.DARK_AQUA + output);

					// has bed
					boolean hasBed = villager.getBedPos() != null;
					output = TextUtils.translate("gui.villager.hasbed") + " " + TextFormatting.WHITE + (hasBed ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
					currentTip.add(TextFormatting.DARK_AQUA + output);

				} else {

					if (tag.hasKey(NBT_TAG_HASVILLAGE)) {

						String villageName = tag.hasKey(NBT_TAG_VILLAGENAME) ? tag.getString(NBT_TAG_VILLAGENAME) : "";
						output = TextUtils.translate("gui.villager.villagename") + " " + TextFormatting.WHITE + villageName;
						currentTip.add(TextFormatting.DARK_AQUA + output);

						if (tag.hasKey(NBT_TAG_BEDPOSITION)) {
							boolean hasBed = true;
							output = TextUtils.translate("gui.villager.hasbed") + " " + TextFormatting.WHITE + (hasBed ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
							currentTip.add(TextFormatting.DARK_AQUA + output);
						}
					}
				}
			}
		}
		
		return currentTip;
	}
	
	@Override
	public List<String> getWailaTail(Entity entity, List<String> currentTip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity entity, NBTTagCompound tag, World world) {
		entity.writeToNBT(tag);

		if (entity instanceof EntityVillagerTek) {
			tag.setInteger(NBT_TAG_DAYSALIVE, ((EntityVillagerTek)entity).getDaysAlive());			
			tag.setBoolean(NBT_TAG_HASVILLAGE, ((EntityVillagerTek)entity).hasVillage());
			
			if (((EntityVillagerTek)entity).hasVillage()) {
				Village village = ((EntityVillagerTek)entity).getVillage();
				tag.setString(NBT_TAG_VILLAGENAME, village.getName());
				
				if (((EntityVillagerTek)entity).getBedPos() != null) {
					tag.setLong(NBT_TAG_BEDPOSITION, ((EntityVillagerTek)entity).getBedPos().toLong());
				}
			}
		}
		
		return tag;
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		TektopiaEntityWaila entityProvider = new TektopiaEntityWaila();
		ArrayList<String> processed = new ArrayList<>();
		
		for (Class<?> entity : getTektopiaEntities()) {
        	String key = entity.getTypeName();
        	
        	if (processed.contains(key)) {
        		continue;
        	}
        	processed.add(key);
        	
        	registrar.registerNBTProvider(entityProvider, entity);
        	
        	registrar.registerHeadProvider(entityProvider, entity);
			registrar.registerBodyProvider(entityProvider, entity);
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaEntitiesRegistered) {
				LoggerUtils.writeLine("Registered WAILA information for entity " + key, true);
			} 
		}
	}
	
	private static ArrayList<Class<?>> getTektopiaEntities() {	
		ArrayList<Class<?>> list = new ArrayList<>();
		
		list.add(EntityVillagerTek.class);
		
		// remove any entities that are inherited from other tektopia mod entities
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.contains(list.get(i).getSuperclass())) {
				list.remove(i);
			}
		}
		
		list.sort(Comparator.comparing(Class::getTypeName));
		list.trimToSize();
		
		return list;
	}
}
