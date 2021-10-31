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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityData {

	protected static final String NBTTAG_VILLAGE_ENTITYID = "villageentityid";
	protected static final String NBTTAG_VILLAGE_ENTITYCLASSNAME = "villageentityclassname";
	protected static final String NBTTAG_VILLAGE_ENTITYMODID = "villageentitymodid";
	protected static final String NBTTAG_VILLAGE_ENTITYMODNAME = "villageentitymodname";
	protected static final String NBTTAG_VILLAGE_ENTITYNAME = "villageentityname";
	protected static final String NBTTAG_VILLAGE_ENTITYLEVEL = "villageentitylevel";
	protected static final String NBTTAG_VILLAGE_ENTITYHEALTH = "villageentityhealth";
	protected static final String NBTTAG_VILLAGE_ENTITYMAXHEALTH = "villageentitymaxhealth";
	protected static final String NBTTAG_VILLAGE_ENTITYHOMEPOSITION = "villageentityhomeposition";
	protected static final String NBTTAG_VILLAGE_ENTITYCURRENTPOSITION = "villageentitycurrentposition";
	protected static final String NBTTAG_VILLAGE_ENTITYTOTALARMOR = "villageentitytotalarmor";
	protected static final String NBTTAG_VILLAGE_ENTITYARMOR = "villageentityarmor";
	protected static final String NBTTAG_VILLAGE_ENTITYEQUIPMENT = "villageentityequipment";

	protected static List<Entity> entityList = null;
	
	protected int id;
	protected String className;
	protected String modId;
	protected String modName;
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
	
	public String getModName() {
		return this.modName;
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
		return Collections.unmodifiableList(this.armor == null ? new ArrayList<ItemStack>() : this.armor);
	}
	
	public List<ItemStack> getEquipment() {
		return Collections.unmodifiableList(this.equipment == null ? new ArrayList<ItemStack>() : this.equipment);
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
		this.modName = "";
		this.name = "";
		this.level = 1;
		this.health = 0;
		this.maxHealth = 20;
		this.homePosition = null;
		this.currentPosition = null;
		this.totalArmorValue = 0;
		
		this.armor = new ArrayList<ItemStack>();
		this.equipment = new ArrayList<ItemStack>();
	}
	
	protected void populateData(EntityVillageNavigator entity) {
		
		if (entity != null) {
			this.id = entity.getEntityId();
			this.className = entity.getClass().getSimpleName().toLowerCase();
			this.modId = ModIdentification.getEntityModId(entity);
			this.modName = ModIdentification.getEntityModName(entity);
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
			this.equipment.removeIf((p) -> p == null || p == ItemStack.EMPTY || p.getItem() == Items.AIR);
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
		this.modName = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYMODNAME) ? nbtTag.getString(NBTTAG_VILLAGE_ENTITYMODNAME) : "";
		this.name = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYNAME) ? nbtTag.getString(NBTTAG_VILLAGE_ENTITYNAME) : "";
		this.level = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYLEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYLEVEL) : 0;
		this.health = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_ENTITYHEALTH) : 0;
		this.maxHealth = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYMAXHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_ENTITYMAXHEALTH) : 0;
		this.homePosition = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYHOMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ENTITYHOMEPOSITION)) : null;
		this.currentPosition = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION)) : null;
		this.totalArmorValue = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYTOTALARMOR) ? nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYTOTALARMOR) : 0; 
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYARMOR)) {
			NBTTagList nbtTagListArmor = nbtTag.getTagList(NBTTAG_VILLAGE_ENTITYARMOR, 10);
			this.armor = new ArrayList<ItemStack>(nbtTagListArmor.tagCount());
			
			for (int index = 0; index < nbtTagListArmor.tagCount(); index++) {
				this.armor.add(new ItemStack(nbtTagListArmor.getCompoundTagAt(index)));
			}
			
			Collections.reverse(this.armor);
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYEQUIPMENT)) {
			NBTTagList nbtTagListEquipment = nbtTag.getTagList(NBTTAG_VILLAGE_ENTITYEQUIPMENT, 10);
			this.equipment = new ArrayList<ItemStack>(nbtTagListEquipment.tagCount());
			
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
		nbtTag.setString(NBTTAG_VILLAGE_ENTITYMODNAME, this.modName);
		nbtTag.setString(NBTTAG_VILLAGE_ENTITYNAME, this.name);
		nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYLEVEL, this.level);
		nbtTag.setFloat(NBTTAG_VILLAGE_ENTITYHEALTH, this.health);
		nbtTag.setFloat(NBTTAG_VILLAGE_ENTITYMAXHEALTH, this.maxHealth);
		if (this.homePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_ENTITYHOMEPOSITION, this.homePosition.toLong());
		}
		if (this.currentPosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION, this.currentPosition.toLong());
		}
		nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYTOTALARMOR, this.totalArmorValue);
		
		if (this.armor != null && this.armor.size() > 0) {
			NBTTagList nbtTagListArmor = new NBTTagList();
			
			for (ItemStack itemStack : this.armor) {
				if (itemStack != null) {
					nbtTagListArmor.appendTag(itemStack.writeToNBT(new NBTTagCompound()));
				}
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_ENTITYARMOR, nbtTagListArmor);
		}
		
		if (this.equipment != null && this.equipment.size() > 0) {
			NBTTagList nbtTagListEquipment = new NBTTagList();
			
			for (ItemStack itemStack : this.equipment) {
				if (itemStack != null && itemStack != ItemStack.EMPTY && itemStack.getItem() != Items.AIR) {
					nbtTagListEquipment.appendTag(itemStack.writeToNBT(new NBTTagCompound()));
				}
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_ENTITYEQUIPMENT, nbtTagListEquipment);
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
