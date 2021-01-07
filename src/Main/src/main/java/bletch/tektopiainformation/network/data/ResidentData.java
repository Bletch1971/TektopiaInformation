package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import bletch.tektopiainformation.TektopiaInformation;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.storage.VillagerInventory;
import net.tangotek.tektopia.structures.VillageStructure;

public class ResidentData {

	private static final String NBTTAG_VILLAGE_RESIDENTID = "villageresidentid";
	private static final String NBTTAG_VILLAGE_RESIDENTNAME = "villageresidentname";
	private static final String NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE = "villageresidentprofessiontype";
	private static final String NBTTAG_VILLAGE_RESIDENTMALE = "villageresidentmale";
	private static final String NBTTAG_VILLAGE_RESIDENTCHILD = "villageresidentchild";
	private static final String NBTTAG_VILLAGE_RESIDENTCAPTAIN = "villageresidentcaptain";
	private static final String NBTTAG_VILLAGE_RESIDENTLEVEL = "villageresidentlevel";
	private static final String NBTTAG_VILLAGE_RESIDENTBASELEVEL = "villageresidentbaselevel";
	private static final String NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL = "villageresidentblessedlevel";
	private static final String NBTTAG_VILLAGE_RESIDENTDAYSALIVE = "villageresidentdaysAlive";
	private static final String NBTTAG_VILLAGE_RESIDENTHEALTH = "villageresidenthealth";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXHEALTH = "villageresidentmaxhealth";
	private static final String NBTTAG_VILLAGE_RESIDENTHUNGER = "villageresidenthunger";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXHUNGER = "villageresidentmaxhunger";
	private static final String NBTTAG_VILLAGE_RESIDENTHAPPY = "villageresidenthappy";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXHAPPY = "villageresidentmaxhappy";
	private static final String NBTTAG_VILLAGE_RESIDENTINTELLIGENCE = "villageresidentintellegience";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE = "villageresidentmaxintellegience";
	private static final String NBTTAG_VILLAGE_RESIDENTHOMEPOSITION = "villageresidenthomeposition";
	private static final String NBTTAG_VILLAGE_RESIDENTBEDPOSITION = "villageresidentbedposition";
	private static final String NBTTAG_VILLAGE_RESIDENTCURRENTPOSITION = "villageresidentcurrentposition";
	private static final String NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE = "villageresidentcurrentstructure";
	private static final String NBTTAG_VILLAGE_RESIDENTADDPROFCOUNT = "villageresidentaddprofcount";
	private static final String NBTTAG_VILLAGE_RESIDENTADDPROF = "villageresidentaddprof";
	private static final String NBTTAG_VILLAGE_RESIDENTTOTALARMOR = "villageresidenttotslarmor";
	private static final String NBTTAG_VILLAGE_RESIDENTARMORCOUNT = "villageresidentarmorcount";
	private static final String NBTTAG_VILLAGE_RESIDENTARMOR = "villageresidentarmor";
	private static final String NBTTAG_VILLAGE_RESIDENTEQUIPMENTCOUNT = "villageresidentequipmentcount";
	private static final String NBTTAG_VILLAGE_RESIDENTEQUIPMENT = "villageresidentequipment";

	@SuppressWarnings("rawtypes")
	private static final List<Class> toolItemClasses = Arrays.asList(ItemAxe.class, ItemHoe.class, ItemSword.class, ItemPickaxe.class, ItemShears.class);
	private static List<Entity> entityList = null;
	
	private int residentId;
	private String residentName;
	private ProfessionType professionType;
	private boolean isMale;
	private boolean isChild;
	private boolean isCaptain;
	private int level;
	private int baseLevel;
	private int blessedLevel;
	private int daysAlive;
	private float health;
	private float maxHealth;
	private int hunger;
	private int maxHunger;
	private int happy;
	private int maxHappy;
	private int intelligence;
	private int maxIntelligence;
	private BlockPos homePosition;
	private BlockPos bedPosition;
	private BlockPos currentPosition;
	private BlockPos currentStructure;	
	private int totalArmorValue;
	
	private Map<ProfessionType, Integer> additionalProfessions;
	private List<ItemStack> armor = null;
	private List<ItemStack> equipment = null;
	
	public ResidentData() {
		populateData(null);
	}
	
	public ResidentData(EntityVillagerTek villager) {
		populateData(villager);
	}
	
	public int getResidentId() {
		return this.residentId;
	}
	
	public String getResidentName() {
		return this.residentName;
	}
	
	public ProfessionType getProfessionType() {
		return this.professionType;
	}
	
	public boolean isMale() {
		return this.isMale;
	}
	
	public boolean isChild() {
		return this.isChild;
	}
	
	public boolean isCaptain() {
		return this.isCaptain;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public int getBaseLevel() {
		return this.baseLevel;
	}
	
	public int getBlessedLevel() {
		return this.blessedLevel;
	}
	
	public int getDaysAlive() {
		return this.daysAlive;
	}
	
	public float getHealth() {
		return this.health;
	}
	
	public float getMaxHealth() {
		return this.maxHealth;
	}
	
	public int getHunger() {
		return this.hunger;
	}
	
	public int getMaxHunger() {
		return this.maxHunger;
	}
	
	public int getHappy() {
		return this.happy;
	}
	
	public int getMaxHappy() {
		return this.maxHappy;
	}
	
	public int getIntelligence() {
		return this.intelligence;
	}
	
	public int getMaxIntelligence() {
		return this.maxIntelligence;
	}
	
	public BlockPos getHomePosition() {
		return this.homePosition;
	}
	
	public BlockPos getBedPosition() {
		return this.bedPosition;
	}
	
	public boolean hasBed() {
		return this.bedPosition != null;
	}
	
	public BlockPos getCurrentPosition() {
		return this.currentPosition;
	}
	
	public BlockPos getCurrentStructure() {
		return this.currentStructure;
	}
	
	public int getTotalArmorValue() {
		return this.totalArmorValue;
	}
	
	public boolean isBlessed() {
		return this.blessedLevel > 0;
	}
	
	public Map<ProfessionType, Integer> getAdditionalProfessions() {
		return this.additionalProfessions == null 
				? Collections.unmodifiableMap(new LinkedHashMap<ProfessionType, Integer>())
				: Collections.unmodifiableMap(this.additionalProfessions.entrySet().stream()
					.sorted((c1 , c2) -> Integer.compare(c2.getValue(), c1.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	public List<ItemStack> getArmor() {
		return Collections.unmodifiableList(this.armor == null ? new ArrayList<ItemStack>() : this.armor);
	}
	
	public List<ItemStack> getEquipment() {
		return Collections.unmodifiableList(this.equipment == null ? new ArrayList<ItemStack>() : this.equipment);
	}
	
	public EntityVillagerTek getVillagerEntity() {
		if (entityList != null && entityList.size() > 0) {
			return (EntityVillagerTek) entityList.stream()
				.filter(e -> e instanceof EntityVillagerTek && e.getEntityId() == this.residentId)
				.findFirst().orElse(null);
		}
		
		return null;
	}
	
	private void clearData() {
		this.residentId = 0;
		this.residentName = "";
		this.professionType = ProfessionType.NITWIT;
		this.isMale = true;
		this.isChild = false;
		this.isCaptain = false;
		this.level = 0;
		this.baseLevel = 0;
		this.blessedLevel = 0;
		this.daysAlive = 0;
		this.health = 0;
		this.maxHealth = 20;
		this.hunger = 0;
		this.maxHunger = 100;
		this.happy = 0;
		this.maxHappy = 100;
		this.intelligence = 0;
		this.maxIntelligence = 100;
		this.homePosition = null;
		this.bedPosition = null;
		this.currentPosition = null;
		this.currentStructure = null;
		this.totalArmorValue = 0;
		
		this.additionalProfessions = new LinkedHashMap<ProfessionType, Integer>();
		this.armor = new ArrayList<ItemStack>();
		this.equipment = new ArrayList<ItemStack>();
	}
	
	public void populateData(EntityVillagerTek villager) {
		clearData();
		
		if (villager != null) {
			ProfessionType primaryProfessionType = villager.getProfessionType();
			
			this.residentId = villager.getEntityId();
			this.residentName = villager.getDisplayName().getFormattedText();
			this.professionType = primaryProfessionType;
			this.isMale = villager.isMale();
			this.isChild = primaryProfessionType == ProfessionType.CHILD || villager.isChild();
			this.isCaptain = primaryProfessionType == ProfessionType.CAPTAIN || villager instanceof EntityGuard && ((EntityGuard)villager).isCaptain();
			this.level = villager.getSkill(primaryProfessionType);
			this.baseLevel = villager.getBaseSkill(primaryProfessionType);
			this.blessedLevel = villager.getBlessed();
			this.daysAlive = villager.getDaysAlive();
			this.health = villager.getHealth();
			this.maxHealth = villager.getMaxHealth();
			this.hunger = villager.getHunger();
			this.maxHunger = villager.getMaxHunger();
			this.happy = villager.getHappy();
			this.maxHappy = villager.getMaxHappy();
			this.intelligence = villager.getIntelligence();
			this.maxIntelligence = villager.getMaxIntelligence();
			this.homePosition = villager.getHomePosition();
			this.bedPosition = villager.getBedPos();
			this.currentPosition = villager.getPosition();
			this.totalArmorValue = villager.getTotalArmorValue();
			
			VillageStructure structure = villager.getCurrentStructure();
			if (structure == null && villager.getVillage() != null) {
				structure = villager.getVillage().getStructure(currentPosition);
			}
			this.currentStructure = structure == null ? null : structure.getFramePos();
			
			for (ProfessionType professionType : TektopiaUtils.getProfessionTypes()) {
				if (professionType == primaryProfessionType) {
					// do not include the villagers main profession
					continue;
				}
				
				int level = villager.getSkill(professionType);
				if (level > 0) {
					this.additionalProfessions.put(professionType, level);
				}
			}

			// populate the armor and equipment list from the villager
			this.armor.addAll((Collection<? extends ItemStack>) villager.getArmorInventoryList());
			this.equipment.addAll((Collection<? extends ItemStack>) villager.getHeldEquipment());
			
			// check if the villager has a tool in their inventory, add to the equipment list
			VillagerInventory inventory = villager.getInventory();
			toolItemClasses.stream()
				.forEach(c -> {
					List<ItemStack> tools = inventory.getItems(
							(Function<ItemStack, Integer>) (s -> {
								return (s.getItem().getClass() == c ? s.getItemDamage() : -1);
							}), 1);
			        
					if (!tools.isEmpty()) {
						this.equipment.addAll(tools);
					}
				});
			
			// remove any invalid items from the equipment list - DO NOT do this for the armor, we need
			this.equipment.removeIf((p) -> p == null || p == ItemStack.EMPTY || p.getItem() == Items.AIR);
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();

		this.residentId = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTID) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTID) : 0;
		this.residentName = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTNAME) ? nbtTag.getString(NBTTAG_VILLAGE_RESIDENTNAME) : "";
		this.professionType = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE) ? ProfessionType.valueOf(nbtTag.getString(NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE)) : ProfessionType.NITWIT;
		this.isMale = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMALE) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTMALE) : true;
		this.isChild = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCHILD) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTCHILD) : false;
		this.isCaptain = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCAPTAIN) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTCAPTAIN) : false;
		this.level = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTLEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTLEVEL) : 0;
		this.baseLevel = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTBASELEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTBASELEVEL) : 0;
		this.blessedLevel = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL) : 0;
		this.daysAlive = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTDAYSALIVE) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTDAYSALIVE) : 0;
		this.health = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_RESIDENTHEALTH) : 0;
		this.maxHealth = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXHEALTH) ? nbtTag.getFloat(NBTTAG_VILLAGE_RESIDENTMAXHEALTH) : 0;
		this.hunger = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTHUNGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTHUNGER) : 0;
		this.maxHunger = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXHUNGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTMAXHUNGER) : 0;
		this.happy = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTHAPPY) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTHAPPY) : 0;
		this.maxHappy = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXHAPPY) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTMAXHAPPY) : 0;
		this.intelligence = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTINTELLIGENCE) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTINTELLIGENCE) : 0;
		this.maxIntelligence = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE) : 0;
		this.homePosition = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTHOMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_RESIDENTHOMEPOSITION)) : null;
		this.bedPosition = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTBEDPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_RESIDENTBEDPOSITION)) : null;
		this.currentPosition = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCURRENTPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_RESIDENTCURRENTPOSITION)) : null;
		this.currentStructure = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE)) : null;
		this.totalArmorValue = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTTOTALARMOR) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTTOTALARMOR) : 0;

		ProfessionType originalProfessionType = this.professionType;
		if (this.isCaptain) {
			this.professionType = ProfessionType.CAPTAIN;
		}
		else if (this.isChild) {
			this.professionType = ProfessionType.CHILD;
		} 

		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTADDPROFCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTADDPROFCOUNT);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_RESIDENTADDPROF + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					String value = nbtTag.getString(key);
					
					if (value != null && value.contains("@")) {
						String[] valueParts = value.split("@");
						
						if (valueParts.length == 2) {
							try {
								ProfessionType professionType = ProfessionType.valueOf(valueParts[0]);
								int level = Integer.parseInt(valueParts[1]);
								this.additionalProfessions.put(professionType, level);
							}
							catch (Exception e) {
								// do nothing if conversion error
							}
						}
					}
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTARMORCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTARMORCOUNT);
			
			this.armor = new ArrayList<ItemStack>(count);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_RESIDENTARMOR + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					NBTTagCompound value = nbtTag.getCompoundTag(key);
					this.armor.add(index, new ItemStack(value));
				} else {
					this.armor.add(index, ItemStack.EMPTY);
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTEQUIPMENTCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTEQUIPMENTCOUNT);
			
			this.equipment = new ArrayList<ItemStack>(count);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_RESIDENTEQUIPMENT + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					NBTTagCompound value = nbtTag.getCompoundTag(key);
					this.equipment.add(index, new ItemStack(value));
				} else {
					this.equipment.add(index, ItemStack.EMPTY);
				}
			}
		}
		
		if (entityList != null && entityList.size() > 0) {
			Entity entity = entityList.stream()
				.filter(e -> e instanceof EntityVillagerTek && e.getEntityId() == this.residentId)
				.findFirst().orElse(null);
			
			if (entity != null) {
				EntityVillagerTek villager = (EntityVillagerTek)entity;
				int blessedLevel = villager.getBlessed();
				if (blessedLevel > 0) {
					this.blessedLevel = blessedLevel;
					this.level = villager.getSkill(originalProfessionType);
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTID, this.residentId);
		nbtTag.setString(NBTTAG_VILLAGE_RESIDENTNAME, this.residentName);
		nbtTag.setString(NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE, this.professionType != null ? this.professionType.name() : ProfessionType.NITWIT.name());
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTMALE, this.isMale);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTCHILD, this.isChild);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTCAPTAIN, this.isCaptain);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTLEVEL, this.level);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTBASELEVEL, this.baseLevel);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL, this.blessedLevel);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTDAYSALIVE, this.daysAlive);
		nbtTag.setFloat(NBTTAG_VILLAGE_RESIDENTHEALTH, this.health);
		nbtTag.setFloat(NBTTAG_VILLAGE_RESIDENTMAXHEALTH, this.maxHealth);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTHUNGER, this.hunger);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTMAXHUNGER, this.maxHunger);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTHAPPY, this.happy);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTMAXHAPPY, this.maxHappy);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTINTELLIGENCE, this.intelligence);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE, this.maxIntelligence);
		if (this.homePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_RESIDENTHOMEPOSITION, this.homePosition.toLong());
		}
		if (this.bedPosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_RESIDENTBEDPOSITION, this.bedPosition.toLong());
		}
		if (this.currentPosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_RESIDENTCURRENTPOSITION, this.currentPosition.toLong());
		}
		if (this.currentStructure != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE, this.currentStructure.toLong());
		}
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTTOTALARMOR, this.totalArmorValue);
		
		if (this.additionalProfessions != null && this.additionalProfessions.size() > 0) {
			nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTADDPROFCOUNT, this.additionalProfessions.size());
			
			int index = 0;
			for (Entry<ProfessionType, Integer> additionalProfession : this.additionalProfessions.entrySet()) {
				if (additionalProfession != null) {
					nbtTag.setString(NBTTAG_VILLAGE_RESIDENTADDPROF + "@" + index, additionalProfession.getKey().name() + "@" + additionalProfession.getValue());
					index++;
				}
			}
		}
		
		if (this.armor != null && this.armor.size() > 0) {
			nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTARMORCOUNT, this.armor.size());
			
			int index = 0;
			for (ItemStack piece : this.armor) {
				if (piece != null && piece != ItemStack.EMPTY && piece.getItem() != Items.AIR) {
					NBTTagCompound nbtTagPiece = new NBTTagCompound();
					piece.writeToNBT(nbtTagPiece);
					
					nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTARMOR + "@" + index, nbtTagPiece);
					index++;
				}
			}
		}
		
		if (this.equipment != null && this.equipment.size() > 0) {
			nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTEQUIPMENTCOUNT, this.equipment.size());
			
			int index = 0;
			for (ItemStack piece : this.equipment) {
				if (piece != null && piece != ItemStack.EMPTY && piece.getItem() != Items.AIR) {
					NBTTagCompound nbtTagPiece = new NBTTagCompound();
					piece.writeToNBT(nbtTagPiece);
					
					nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTEQUIPMENT + "@" + index, nbtTagPiece);
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
