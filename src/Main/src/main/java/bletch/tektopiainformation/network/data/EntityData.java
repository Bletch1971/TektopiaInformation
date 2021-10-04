package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import bletch.tektopiainformation.TektopiaInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityData {

	private static final String NBTTAG_VILLAGE_ENTITYID = "villageentityid";
	private static final String NBTTAG_VILLAGE_ENTITYNAME = "villageentityname";
	private static final String NBTTAG_VILLAGE_ENTITYHEALTH = "villageentityhealth";
	private static final String NBTTAG_VILLAGE_ENTITYMAXHEALTH = "villageentitymaxhealth";
	private static final String NBTTAG_VILLAGE_ENTITYHOMEPOSITION = "villageentityhomeposition";
	private static final String NBTTAG_VILLAGE_ENTITYCURRENTPOSITION = "villageentitycurrentposition";
	private static final String NBTTAG_VILLAGE_ENTITYTOTALARMOR = "villageentitytotslarmor";
	private static final String NBTTAG_VILLAGE_ENTITYARMORCOUNT = "villageentityarmorcount";
	private static final String NBTTAG_VILLAGE_ENTITYARMOR = "villageentityarmor";
	private static final String NBTTAG_VILLAGE_ENTITYEQUIPMENTCOUNT = "villageentityequipmentcount";
	private static final String NBTTAG_VILLAGE_ENTITYEQUIPMENT = "villageentityequipment";

	protected static List<Entity> entityList = null;
	
	private int id;
	private String name;
	private float health;
	private float maxHealth;
	private BlockPos homePosition;
	private BlockPos currentPosition;	
	private int totalArmorValue;
	
	private List<ItemStack> armor = null;
	protected List<ItemStack> equipment = null;
	
	protected EntityData() {
		populateData(null);
	}
	
	protected EntityData(EntityVillageNavigator entity) {
		populateData(entity);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
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
		this.name = "";
		this.health = 0;
		this.maxHealth = 20;
		this.homePosition = null;
		this.currentPosition = null;
		this.totalArmorValue = 0;
		
		this.armor = new ArrayList<ItemStack>();
		this.equipment = new ArrayList<ItemStack>();
	}
	
	protected void populateData(EntityVillageNavigator entity) {
		clearData();
		
		if (entity != null) {
			this.id = entity.getEntityId();
			this.name = entity.getDisplayName().getFormattedText();
			this.health = entity.getHealth();
			this.maxHealth = entity.getMaxHealth();
			this.homePosition = entity.getHomePosition();
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
		this.name = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYNAME) ? nbtTag.getString(NBTTAG_VILLAGE_ENTITYNAME) : "";
		this.health = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_ENTITYHEALTH) : 0;
		this.maxHealth = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYMAXHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_ENTITYMAXHEALTH) : 0;
		this.homePosition = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYHOMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ENTITYHOMEPOSITION)) : null;
		this.currentPosition = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ENTITYCURRENTPOSITION)) : null;
		this.totalArmorValue = nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYTOTALARMOR) ? nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYTOTALARMOR) : 0; 
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYARMORCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYARMORCOUNT);
			
			this.armor = new ArrayList<ItemStack>(count);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_ENTITYARMOR + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					NBTTagCompound value = nbtTag.getCompoundTag(key);
					this.armor.add(index, new ItemStack(value));
				} else {
					this.armor.add(index, ItemStack.EMPTY);
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENTITYEQUIPMENTCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_ENTITYEQUIPMENTCOUNT);
			
			this.equipment = new ArrayList<ItemStack>(count);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_ENTITYEQUIPMENT + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					NBTTagCompound value = nbtTag.getCompoundTag(key);
					this.equipment.add(index, new ItemStack(value));
				} else {
					this.equipment.add(index, ItemStack.EMPTY);
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYID, this.id);
		nbtTag.setString(NBTTAG_VILLAGE_ENTITYNAME, this.name);
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
			nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYARMORCOUNT, this.armor.size());
			
			int index = 0;
			for (ItemStack piece : this.armor) {
				if (piece != null && piece != ItemStack.EMPTY && piece.getItem() != Items.AIR) {
					NBTTagCompound nbtTagPiece = new NBTTagCompound();
					piece.writeToNBT(nbtTagPiece);
					
					nbtTag.setTag(NBTTAG_VILLAGE_ENTITYARMOR + "@" + index, nbtTagPiece);
					index++;
				}
			}
		}
		
		if (this.equipment != null && this.equipment.size() > 0) {
			nbtTag.setInteger(NBTTAG_VILLAGE_ENTITYEQUIPMENTCOUNT, this.equipment.size());
			
			int index = 0;
			for (ItemStack piece : this.equipment) {
				if (piece != null && piece != ItemStack.EMPTY && piece.getItem() != Items.AIR) {
					NBTTagCompound nbtTagPiece = new NBTTagCompound();
					piece.writeToNBT(nbtTagPiece);
					
					nbtTag.setTag(NBTTAG_VILLAGE_ENTITYEQUIPMENT + "@" + index, nbtTagPiece);
					index++;
				}
			}
		}
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
