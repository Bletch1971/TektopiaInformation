package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import bletch.common.utils.ModIdentification;
import bletch.tektopiainformation.TektopiaInformation;
import bletch.tektopiainformation.core.ModDetails;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityData {

	protected static final String NBTTAG_VILLAGE_ENTITYID = "id";
	protected static final String NBTTAG_VILLAGE_ENTITYCLASSNAME = "class";
	protected static final String NBTTAG_VILLAGE_ENTITYMODID = "modid";
	protected static final String NBTTAG_VILLAGE_ENTITYNAME = "name";
	protected static final String NBTTAG_VILLAGE_ENTITYLEVEL = "lvl";
	protected static final String NBTTAG_VILLAGE_ENTITYHEALTH = "health";
	protected static final String NBTTAG_VILLAGE_ENTITYMAXHEALTH = "maxhealth";
	protected static final String NBTTAG_VILLAGE_ENTITYHOMEPOSITION = "home";
	protected static final String NBTTAG_VILLAGE_ENTITYCURRENTPOSITION = "pos";
	protected static final String NBTTAG_VILLAGE_ENTITYTOTALARMOR = "armor";
	protected static final String NBTTAG_VILLAGE_ENTITYARMOR = "armorlist";
	protected static final String NBTTAG_VILLAGE_ENTITYEQUIPMENT = "equiplist";

	protected static List<Entity> entityList = null;
	
	protected int id;
	protected String className;
	protected String modId;
	protected String name;
	protected int level;
	protected float health;
	protected float maxHealth;
	protected BlockPos homePosition;
	protected BlockPos currentPosition;	
	protected int totalArmorValue;
	
	protected List<ItemStack> armor = null;
	protected List<ItemStack> equipment = null;
	
	protected EntityData() {
		clearData();
	}
	
	protected EntityData(EntityVillageNavigator entity, Boolean populateEntity) {
		clearData();
		
		if (populateEntity)
			populateData(entity);
	}
	
	protected EntityData(NBTTagCompound nbtTag) {
		readNBT(nbtTag);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	public String getModId() {
		return this.modId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public float getHealth() {
		return this.health;
	}
	
	public float getMaxHealth() {
		return this.maxHealth;
	}
	
	public BlockPos getHomePosition() {
		return this.homePosition;
	}
	
	public BlockPos getCurrentPosition() {
		return this.currentPosition;
	}
	
	public int getTotalArmorValue() {
		return this.totalArmorValue;
	}
	
	public List<ItemStack> getArmor() {
		return Collections.unmodifiableList(this.armor == null ? new ArrayList<>() : this.armor);
	}
	
	public List<ItemStack> getEquipment() {
		return Collections.unmodifiableList(this.equipment == null ? new ArrayList<>() : this.equipment);
	}
	
	public EntityVillageNavigator getVillagerEntity() {
		if (entityList != null && entityList.size() > 0) {
			return (EntityVillageNavigator) entityList.stream()
				.filter(e -> e instanceof EntityVillagerTek && e.getEntityId() == this.id)
				.findFirst().orElse(null);
		}
		
		return null;
	}
	
	protected void clearData() {
		
		this.id = 0;
		this.className = "";
		this.modId = ModDetails.MOD_ID;
		this.name = "";
		this.level = 0;
		this.health = 0;
		this.maxHealth = 20;
		this.homePosition = null;
		this.currentPosition = null;
		this.totalArmorValue = 0;
		
		this.armor = new ArrayList<>();
		this.equipment = new ArrayList<>();
	}
	
	protected void populateData(EntityVillageNavigator entity) {
		
		if (entity != null) {
			this.id = entity.getEntityId();
			this.className = entity.getClass().getSimpleName().toLowerCase();
			this.modId = ModIdentification.getEntityModId(entity);
			this.level = 1;
			this.name = entity.getName();
			this.health = entity.getHealth();
			this.maxHealth = entity.getMaxHealth();
			// removed this so we can use this for other entities that do not have beds assigned (architect, tradesman, etc)
			//this.homePosition = entity.getHomePosition();
			this.currentPosition = entity.getPosition();
			this.totalArmorValue = entity.getTotalArmorValue();

			// populate the armor and equipment list from the entity
			this.armor.addAll((Collection<? extends ItemStack>) entity.getArmorInventoryList());
			this.equipment.addAll((Collection<? extends ItemStack>) entity.getHeldEquipment());
			
			// remove any invalid items from the equipment list - DO NOT do this for the armor, we need
			this.equipment.removeIf((i) -> i == null || i.isEmpty());
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();

		this.id = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYID) ? nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYID) : 0;
		this.className = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYCLASSNAME) ? nbtTag.getString(NBTTAG_VILLAGE_ENTITYCLASSNAME) : "";
		this.modId = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYMODID) ? nbtTag.getString(NBTTAG_VILLAGE_ENTITYMODID) : ModDetails.MOD_ID;
		this.name = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYNAME) ? nbtTag.getString(NBTTAG_VILLAGE_ENTITYNAME) : "";
		this.level = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYLEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYLEVEL) : 0;
		this.health = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_ENTITYHEALTH) : 0;
		this.maxHealth = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYMAXHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_ENTITYMAXHEALTH) : 0;
		this.homePosition = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYHOMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ENTITYHOMEPOSITION)) : null;
		this.currentPosition = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION)) : null;
		this.totalArmorValue = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYTOTALARMOR) ? nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYTOTALARMOR) : 0; 
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYARMOR)) {
			NBTTagList nbtTagListArmor = nbtTag.getTagList(NBTTAG_VILLAGE_ENTITYARMOR, 10);
			this.armor = new ArrayList<>(nbtTagListArmor.tagCount());
			
			for (int index = 0; index < nbtTagListArmor.tagCount(); index++) {
				NBTTagCompound nbtTagArmorItem = nbtTagListArmor.getCompoundTagAt(index);
				if (nbtTagArmorItem.hasNoTags()) {
					this.armor.add(ItemStack.EMPTY);
				} else {
					this.armor.add(new ItemStack(nbtTagArmorItem));
				}
			}
			
			Collections.reverse(this.armor);
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYEQUIPMENT)) {
			NBTTagList nbtTagListEquipment = nbtTag.getTagList(NBTTAG_VILLAGE_ENTITYEQUIPMENT, 10);
			this.equipment = new ArrayList<>(nbtTagListEquipment.tagCount());
			
			for (int index = 0; index < nbtTagListEquipment.tagCount(); index++) {
				this.equipment.add(new ItemStack(nbtTagListEquipment.getCompoundTagAt(index)));
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYID, this.id);
		nbtTag.setString(NBTTAG_VILLAGE_ENTITYCLASSNAME, this.className);
		nbtTag.setString(NBTTAG_VILLAGE_ENTITYMODID, this.modId);
		nbtTag.setString(NBTTAG_VILLAGE_ENTITYNAME, this.name);
		if (this.level > 0) {
			nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYLEVEL, this.level);
		}
		if (this.health > 0) {
			nbtTag.setFloat(NBTTAG_VILLAGE_ENTITYHEALTH, this.health);
		}
		if (this.maxHealth > 0) {
			nbtTag.setFloat(NBTTAG_VILLAGE_ENTITYMAXHEALTH, this.maxHealth);
		}
		if (this.homePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_ENTITYHOMEPOSITION, this.homePosition.toLong());
		}
		if (this.currentPosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION, this.currentPosition.toLong());
		}
		if (this.totalArmorValue > 0) {
			nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYTOTALARMOR, this.totalArmorValue);
		}
		
		if (this.armor != null && this.armor.size() > 0) {
			NBTTagList nbtTagListArmor = new NBTTagList();
			
			int emptyCount = 0;
			
			for (ItemStack itemStack : this.armor) {
				if (itemStack != null) {
					if (itemStack.isEmpty()) {
						nbtTagListArmor.appendTag(new NBTTagCompound());
						emptyCount++;
					} else {
						NBTTagCompound nbtTagArmorItem = itemStack.writeToNBT(new NBTTagCompound());
						nbtTagListArmor.appendTag(nbtTagArmorItem);
					}
				}
			}
			
			if (emptyCount != nbtTagListArmor.tagCount() && !nbtTagListArmor.hasNoTags()) {
				nbtTag.setTag(NBTTAG_VILLAGE_ENTITYARMOR, nbtTagListArmor);
			}
		}
		
		if (this.equipment != null && this.equipment.size() > 0) {
			NBTTagList nbtTagListEquipment = new NBTTagList();
			
			for (ItemStack itemStack : this.equipment) {
				if (itemStack != null && !itemStack.isEmpty()) {
					NBTTagCompound nbtTagEquipmentItem = itemStack.writeToNBT(new NBTTagCompound());
					if (!nbtTagEquipmentItem.hasNoTags()) {
						nbtTagListEquipment.appendTag(nbtTagEquipmentItem);
					}
				}
			}
			
			if (!nbtTagListEquipment.hasNoTags()) {
				nbtTag.setTag(NBTTAG_VILLAGE_ENTITYEQUIPMENT, nbtTagListEquipment);
			}
		}
		
		return nbtTag;
	}
	
	public static void resetEntityList() {
		entityList = null;

		// check if we are client side only
		if (TektopiaInformation.proxy.isRemote()) {
			// get a list of the entities - client side
			entityList = Minecraft.getMinecraft().world.getLoadedEntityList();
		}
	}

}
