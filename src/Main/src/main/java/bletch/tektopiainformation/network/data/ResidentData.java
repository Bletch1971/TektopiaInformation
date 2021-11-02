package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipeList;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.storage.VillagerInventory;
import net.tangotek.tektopia.structures.VillageStructure;

public class ResidentData extends EntityData {

	protected static final String NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE = "villageresidentprofessiontype";
	protected static final String NBTTAG_VILLAGE_RESIDENTMALE = "villageresidentmale";
	protected static final String NBTTAG_VILLAGE_RESIDENTCHILD = "villageresidentchild";
	protected static final String NBTTAG_VILLAGE_RESIDENTCAPTAIN = "villageresidentcaptain";
	protected static final String NBTTAG_VILLAGE_RESIDENTVENDOR = "villageresidentvendor";
	protected static final String NBTTAG_VILLAGE_RESIDENTVISITOR = "villageresidentvisitor";
	protected static final String NBTTAG_VILLAGE_RESIDENTSLEEPING = "villageresidentsleeping";
	protected static final String NBTTAG_VILLAGE_RESIDENTBASELEVEL = "villageresidentbaselevel";
	protected static final String NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL = "villageresidentblessedlevel";
	protected static final String NBTTAG_VILLAGE_RESIDENTDAYSALIVE = "villageresidentdaysAlive";
	protected static final String NBTTAG_VILLAGE_RESIDENTHUNGER = "villageresidenthunger";
	protected static final String NBTTAG_VILLAGE_RESIDENTMAXHUNGER = "villageresidentmaxhunger";
	protected static final String NBTTAG_VILLAGE_RESIDENTHAPPY = "villageresidenthappy";
	protected static final String NBTTAG_VILLAGE_RESIDENTMAXHAPPY = "villageresidentmaxhappy";
	protected static final String NBTTAG_VILLAGE_RESIDENTINTELLIGENCE = "villageresidentintellegience";
	protected static final String NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE = "villageresidentmaxintellegience";

	protected static final String NBTTAG_VILLAGE_RESIDENTWORKSTART = "villageresidentworkstart";
	protected static final String NBTTAG_VILLAGE_RESIDENTWORKFINISH = "villageresidentworkfinish";
	protected static final String NBTTAG_VILLAGE_RESIDENTSLEEPSTART = "villageresidentsleepstart";
	protected static final String NBTTAG_VILLAGE_RESIDENTSLEEPFINISH = "villageresidentsleepfinish";
	
	protected static final String NBTTAG_VILLAGE_RESIDENTCANHAVEBED = "villageresidentcanhavebed";
	protected static final String NBTTAG_VILLAGE_RESIDENTBEDPOSITION = "villageresidentbedposition";
	protected static final String NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE = "villageresidentcurrentstructure";
	protected static final String NBTTAG_VILLAGE_RESIDENTCURRENTTASK = "villageresidentcurrenttask";
	protected static final String NBTTAG_VILLAGE_RESIDENTADDPROF = "villageresidentaddprof";
	protected static final String NBTTAG_VILLAGE_RESIDENTADDPROFNAME = "villageresidentaddprofname";
	protected static final String NBTTAG_VILLAGE_RESIDENTADDPROFLEVEL = "villageresidentaddproflevel";
	protected static final String NBTTAG_VILLAGE_RESIDENTAIFILTER = "villageresidentaifilter";
	protected static final String NBTTAG_VILLAGE_RESIDENTAIFILTERNAME = "villageresidentaifiltername";
	protected static final String NBTTAG_VILLAGE_RESIDENTAIFILTERENABLED = "villageresidentaifilterenabled";
	protected static final String NBTTAG_VILLAGE_RESIDENTRECIPES = "villageresidentrecipes";
	protected static final String NBTTAG_VILLAGE_RESIDENTINVENTORY = "villageresidentinventory";
	protected static final String NBTTAG_VILLAGE_RESIDENTRECENTEATS = "villageresidentrecenteats";

	@SuppressWarnings("rawtypes")
	protected static final List<Class> toolItemClasses = Arrays.asList(ItemAxe.class, ItemHoe.class, ItemSword.class, ItemPickaxe.class, ItemShears.class);
	
	protected String professionType;
	protected boolean isMale;
	protected boolean isChild;
	protected boolean isCaptain;
	protected boolean isVendor;
	protected boolean isVisitor;
	protected boolean isSleeping;
	protected int baseLevel;
	protected int blessedLevel;
	protected int daysAlive;
	protected int hunger;
	protected int maxHunger;
	protected int happy;
	protected int maxHappy;
	protected int intelligence;
	protected int maxIntelligence;
	protected int workStartTime;
	protected int workFinishTime;
	protected int sleepStartTime;
	protected int sleepFinishTime;
	
	protected Boolean canHaveBed;
	protected BlockPos bedPosition;
	protected BlockPos currentStructure;
	protected String currentTask;
	
	protected Map<String, Integer> additionalProfessions;
	protected Map<String, Boolean> aiFilters;
	protected MerchantRecipeList recipes;
	protected List<ItemStack> inventory;
	protected List<Integer> recentEats;
	
	protected ResidentData() {
		super();
	}
	
	public ResidentData(EntityVillagerTek villager) {
		super(villager, false);
		
		populateData(villager);
	}
	
	protected ResidentData(EntityVillagerTek villager, Boolean populateVillager) {
		super(villager, false);
		
		if (populateVillager)
			populateData(villager);
	}
	
	public ResidentData(NBTTagCompound nbtTag) {
		super();
		
		readNBT(nbtTag);
	}
	
	public String getProfessionType() {
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
	
	public boolean isVendor() {
		return this.isVendor;
	}
	
	public boolean isVisitor() {
		return this.isVisitor;
	}
	
	public boolean isSleeping() {
		return this.isSleeping;
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

	public int getWorkStartSeconds() {
		return (int) Math.max(0.0F, ((float)this.workStartTime) / VillageData.MC_TICKS_PER_SECOND);
	}

	public int getWorkStartTicks() {
		return (int) Math.max(0.0F, (float)this.workStartTime);
	}

	public int getWorkFinishSeconds() {
		return (int) Math.max(0.0F, ((float)this.workFinishTime) / VillageData.MC_TICKS_PER_SECOND);
	}

	public int getWorkFinishTicks() {
		return (int) Math.max(0.0F, (float)this.workFinishTime);
	}

	public int getSleepStartSeconds() {
		return (int) Math.max(0.0F, ((float)this.sleepStartTime) / VillageData.MC_TICKS_PER_SECOND);
	}

	public int getSleepStartTicks() {
		return (int) Math.max(0.0F, (float)this.sleepStartTime);
	}

	public int getSleepFinishSeconds() {
		return (int) Math.max(0.0F, ((float)this.sleepFinishTime) / VillageData.MC_TICKS_PER_SECOND);
	}

	public int getSleepFinishTicks() {
		return (int) Math.max(0.0F, (float)this.sleepFinishTime);
	}
	
	public boolean getCanHaveBed() {
		return this.canHaveBed;
	}
	
	public BlockPos getBedPosition() {
		return this.bedPosition;
	}
	
	public boolean hasBed() {
		return this.bedPosition != null;
	}
	
	public BlockPos getCurrentStructure() {
		return this.currentStructure;
	}
	
	public String getCurrentTask() {
		return this.currentTask;
	}
	
	public boolean isBlessed() {
		return this.blessedLevel > 0;
	}
	
	public Map<String, Integer> getAdditionalProfessions() {
		return this.additionalProfessions == null 
				? Collections.unmodifiableMap(new LinkedHashMap<String, Integer>())
				: Collections.unmodifiableMap(this.additionalProfessions.entrySet().stream()
					.sorted((c1 , c2) -> Integer.compare(c2.getValue(), c1.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	public int getAdditionalProfessionsCount() {
		return this.additionalProfessions == null 
				? 0 
				: this.additionalProfessions.size();
	}
	
	public Map<String, Boolean> getAiFilters() {
		return this.aiFilters == null 
				? Collections.unmodifiableMap(new LinkedHashMap<String, Boolean>())
				: Collections.unmodifiableMap(this.aiFilters.entrySet().stream()
					.sorted((c1 , c2) -> c1.getKey().compareTo(c2.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	public int getAiFiltersCount() {
		return this.aiFilters == null 
				? 0 
				: this.aiFilters.size();
	}
	
	public MerchantRecipeList getRecipeList() {
		return this.recipes == null
				? new MerchantRecipeList()
				: this.recipes;
	}
	
	public List<ItemStack> getInventory() {
		return this.inventory == null
				? new ArrayList<ItemStack>()
				: this.inventory;
	}
	
	public int getInventoryCount() {
		return this.inventory == null 
				? 0 
				: this.inventory.size();
	}
	
	public List<ItemStack> getRecentEats() {
		return this.recentEats.stream()
				.map(itemId -> new ItemStack(Item.getItemById(itemId)))
				.collect(Collectors.toList());
	}
	
	public EntityVillagerTek getVillagerEntity() {
		if (entityList != null && entityList.size() > 0) {
			return (EntityVillagerTek) entityList.stream()
				.filter(e -> e instanceof EntityVillagerTek && e.getEntityId() == getId())
				.findFirst().orElse(null);
		}
		
		return null;
	}
	
	protected void clearData() {
		super.clearData();
		
		this.professionType = null;
		this.isMale = true;
		this.isChild = false;
		this.isCaptain = false;
		this.isVendor = false;
		this.isVisitor = false;
		this.isSleeping = false;
		this.baseLevel = 0;
		this.blessedLevel = 0;
		this.daysAlive = 0;
		this.hunger = 0;
		this.maxHunger = 100;
		this.happy = 0;
		this.maxHappy = 100;
		this.intelligence = 0;
		this.maxIntelligence = 100;
		this.workStartTime = 0;
		this.workFinishTime = 0;
		this.sleepStartTime = 0;
		this.sleepFinishTime = 0;
		
		this.canHaveBed = true;
		this.bedPosition = null;
		this.currentStructure = null;
		this.currentTask = null;
		
		this.additionalProfessions = new LinkedHashMap<String, Integer>();
		this.aiFilters = new LinkedHashMap<String, Boolean>(); 
		this.recipes = null;
		this.inventory = null;
		this.recentEats = new ArrayList<Integer>();
	}
	
	protected void populateData(EntityVillagerTek villager) {
		super.populateData(villager);
		
		if (villager != null) {
			ProfessionType villagerProfessionType = villager.getProfessionType();
			
			if (villagerProfessionType != null) {
				this.professionType = villagerProfessionType.name();
			}
			this.isMale = villager.isMale();
			if (villagerProfessionType != null) {
				this.isChild = villagerProfessionType == ProfessionType.CHILD || villager.isChild();
			}
			if (villagerProfessionType != null) {
				this.isCaptain = villagerProfessionType == ProfessionType.CAPTAIN || villager instanceof EntityGuard && ((EntityGuard)villager).isCaptain();
			}
			this.isVendor = villager.isRole(VillagerRole.VENDOR);
			this.isVisitor = villager.isRole(VillagerRole.VISITOR);
			this.isSleeping = villager.isSleeping();
			if (villagerProfessionType != null) {
				this.level = villager.getSkill(villagerProfessionType);
			}
			if (villagerProfessionType != null) {
				this.baseLevel = villager.getBaseSkill(villagerProfessionType);
			}
			this.blessedLevel = villager.getBlessed();
			this.daysAlive = villager.getDaysAlive();
			this.hunger = villager.getHunger();
			this.maxHunger = villager.getMaxHunger();
			this.happy = villager.getHappy();
			this.maxHappy = villager.getMaxHappy();
			this.intelligence = villager.getIntelligence();
			this.maxIntelligence = villager.getMaxIntelligence();
			
			int sleepOffset = TektopiaUtils.getVillagerSleepOffset(villager);
			this.workStartTime = TektopiaUtils.fixTime(EntityVillagerTek.WORK_START_TIME + sleepOffset);
			this.workFinishTime = TektopiaUtils.fixTime(EntityVillagerTek.WORK_END_TIME + sleepOffset);
			this.sleepStartTime = TektopiaUtils.fixTime(EntityVillagerTek.SLEEP_START_TIME + sleepOffset);
			this.sleepFinishTime = TektopiaUtils.fixTime(EntityVillagerTek.SLEEP_END_TIME + sleepOffset);
			
			this.bedPosition = villager.getBedPos();
			
			VillageStructure structure = villager.getCurrentStructure();
			if (structure == null && villager.getVillage() != null) {
				structure = villager.getVillage().getStructure(getCurrentPosition());
			}
			this.currentStructure = structure == null ? null : structure.getFramePos();
			
			for (EntityAITaskEntry task : villager.tasks.taskEntries) {
				if (task != null && task.action != null && task.using) {
					this.currentTask = task.action.getClass().getSimpleName();
				}
			}
			
			for (String professionType : TektopiaUtils.getProfessionTypeNames(false)) {
				if (professionType == this.professionType) {
					// do not include the villagers main profession
					continue;
				}
				
				try {
					int level = villager.getSkill(ProfessionType.valueOf(professionType));
					if (level > 0) {
						this.additionalProfessions.put(professionType, level);
					}
				}
				catch (Exception ex) {
					//do nothing if an error was encountered
				}
			}
			
			List<String> aiFilters = villager.getAIFilters();
			if (aiFilters != null && aiFilters.size() > 0) {
				for (String aiFilter : aiFilters) {
					this.aiFilters.put(aiFilter, villager.isAIFilterEnabled(aiFilter));
				}
			}
			
			if (villager instanceof EntityArchitect) {
				this.name = villager.getName();
				this.professionType = TektopiaUtils.PROFESSIONTYPE_ARCHITECT;
				this.homePosition = this.currentStructure;
				this.canHaveBed = false;
			}
			
			if (villager instanceof EntityTradesman) {
				this.name = villager.getName();
				this.professionType = TektopiaUtils.PROFESSIONTYPE_TRADESMAN;
				this.homePosition = this.currentStructure;
				this.canHaveBed = false;
			}
			
			if (villager.isRole(VillagerRole.VISITOR)) {
				if (villagerProfessionType == null) {
					String className = villager.getClass().getSimpleName().toUpperCase();
					if (className.startsWith("ENTITY")) {
						this.professionType = className.substring("ENTITY".length());
					}
				}				
				this.homePosition = null;
				this.canHaveBed = false;
			}
			
			if (villager instanceof IMerchant) {
				this.recipes = ((IMerchant)villager).getRecipes(null);
			}
			
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
			this.equipment.removeIf((i) -> i == null || i.isEmpty());
			
			int inventorySize = inventory.getSizeInventory();
			if (inventorySize > 0) {
				this.inventory = new ArrayList<ItemStack>(inventorySize);
				
				for (int slot = 0; slot < inventorySize; slot++) {
					this.inventory.add(inventory.getStackInSlot(slot).copy());
				}
			}
			
			this.recentEats.addAll(TektopiaUtils.getVillagerRecentEats(villager));
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		super.readNBT(nbtTag);

		this.professionType = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE) ? nbtTag.getString(NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE) : null;
		this.isMale = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMALE) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTMALE) : true;
		this.isChild = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCHILD) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTCHILD) : false;
		this.isCaptain = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCAPTAIN) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTCAPTAIN) : false;
		this.isVendor = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTVENDOR) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTVENDOR) : false;
		this.isVisitor = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTVISITOR) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTVISITOR) : false;
		this.isSleeping = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTSLEEPING) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTSLEEPING) : false;
		this.baseLevel = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTBASELEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTBASELEVEL) : 0;
		this.blessedLevel = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL) : 0;
		this.daysAlive = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTDAYSALIVE) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTDAYSALIVE) : 0;
		this.hunger = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTHUNGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTHUNGER) : 0;
		this.maxHunger = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXHUNGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTMAXHUNGER) : 0;
		this.happy = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTHAPPY) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTHAPPY) : 0;
		this.maxHappy = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXHAPPY) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTMAXHAPPY) : 0;
		this.intelligence = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTINTELLIGENCE) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTINTELLIGENCE) : 0;
		this.maxIntelligence = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE) : 0;
		this.workStartTime = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTWORKSTART) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTWORKSTART) : 0;
		this.workFinishTime = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTWORKFINISH) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTWORKFINISH) : 0;
		this.sleepStartTime = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTSLEEPSTART) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTSLEEPSTART) : 0;
		this.sleepFinishTime = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTSLEEPFINISH) ? nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTSLEEPFINISH) : 0;
		
		this.canHaveBed = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCANHAVEBED) ? nbtTag.getBoolean(NBTTAG_VILLAGE_RESIDENTCANHAVEBED) : true;
		this.bedPosition = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTBEDPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_RESIDENTBEDPOSITION)) : null;
		this.currentStructure = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE)) : null;
		this.currentTask = nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTCURRENTTASK) ? nbtTag.getString(NBTTAG_VILLAGE_RESIDENTCURRENTTASK) : null;
		
		ProfessionType originalProfessionType = null;
		if (this.professionType != null && this.professionType.trim() != "") {
			originalProfessionType = TektopiaUtils.getProfessionType(this.professionType);
		}
		
		if (this.isCaptain) {
			this.professionType = ProfessionType.CAPTAIN.name();
		}
		else if (this.isChild) {
			this.professionType = ProfessionType.CHILD.name();
		} 

		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTADDPROF)) {
			NBTTagList nbtTagListProfessions = nbtTag.getTagList(NBTTAG_VILLAGE_RESIDENTADDPROF, 10);
			
			for (int index = 0; index < nbtTagListProfessions.tagCount(); index++) {
				NBTTagCompound nbtTagProfession = nbtTagListProfessions.getCompoundTagAt(index);
				
				this.additionalProfessions.put(nbtTagProfession.getString(NBTTAG_VILLAGE_RESIDENTADDPROFNAME), nbtTagProfession.getInteger(NBTTAG_VILLAGE_RESIDENTADDPROFLEVEL));
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTAIFILTER)) {
			NBTTagList nbtTagListAIFilters = nbtTag.getTagList(NBTTAG_VILLAGE_RESIDENTAIFILTER, 10);
			
			for (int index = 0; index < nbtTagListAIFilters.tagCount(); index++) {
				NBTTagCompound nbtTagAIFilter = nbtTagListAIFilters.getCompoundTagAt(index);
				
				this.aiFilters.put(nbtTagAIFilter.getString(NBTTAG_VILLAGE_RESIDENTAIFILTERNAME), nbtTagAIFilter.getBoolean(NBTTAG_VILLAGE_RESIDENTAIFILTERENABLED));
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTRECIPES)) {
			if (this.recipes == null)
				this.recipes = new MerchantRecipeList();
			this.recipes.readRecipiesFromTags(nbtTag.getCompoundTag(NBTTAG_VILLAGE_RESIDENTRECIPES));
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTINVENTORY)) {
			NBTTagList nbtTagListInventory = nbtTag.getTagList(NBTTAG_VILLAGE_RESIDENTINVENTORY, 10);		
			this.inventory = new ArrayList<ItemStack>(nbtTagListInventory.tagCount());
			
			for (int index = 0; index < nbtTagListInventory.tagCount(); index++) {
				this.inventory.add(new ItemStack(nbtTagListInventory.getCompoundTagAt(index)));
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTRECENTEATS)) {
			NBTTagList nbtTagListRecentEats = nbtTag.getTagList(NBTTAG_VILLAGE_RESIDENTRECENTEATS, 3);
			
			for (int index = 0; index < nbtTagListRecentEats.tagCount(); index++) {
				int nbtTagId = nbtTagListRecentEats.getIntAt(index);
				
				this.recentEats.add(nbtTagId);
			}
		}
		
		if (originalProfessionType != null) {
			if (entityList != null && entityList.size() > 0) {
				Entity entity = entityList.stream()
					.filter(e -> e instanceof EntityVillagerTek && e.getEntityId() == getId())
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
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		nbtTag = super.writeNBT(nbtTag);
		
		if (this.professionType != null && this.professionType.trim() != "") {
			nbtTag.setString(NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE, this.professionType);
		}
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTMALE, this.isMale);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTCHILD, this.isChild);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTCAPTAIN, this.isCaptain);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTVENDOR, this.isVendor);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTVISITOR, this.isVisitor);
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTSLEEPING, this.isSleeping);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTBASELEVEL, this.baseLevel);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL, this.blessedLevel);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTDAYSALIVE, this.daysAlive);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTHUNGER, this.hunger);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTMAXHUNGER, this.maxHunger);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTHAPPY, this.happy);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTMAXHAPPY, this.maxHappy);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTINTELLIGENCE, this.intelligence);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE, this.maxIntelligence);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTWORKSTART, this.workStartTime);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTWORKFINISH, this.workFinishTime);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTSLEEPSTART, this.sleepStartTime);
		nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTSLEEPFINISH, this.sleepFinishTime);
		
		nbtTag.setBoolean(NBTTAG_VILLAGE_RESIDENTCANHAVEBED, this.canHaveBed);
		if (this.bedPosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_RESIDENTBEDPOSITION, this.bedPosition.toLong());
		}
		if (this.currentStructure != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE, this.currentStructure.toLong());
		}
		if (this.currentTask != null) {
			nbtTag.setString(NBTTAG_VILLAGE_RESIDENTCURRENTTASK, this.currentTask);
		}
		
		if (this.additionalProfessions != null && this.additionalProfessions.size() > 0) {
			NBTTagList nbtTagListProfessions = new NBTTagList();
			
			for (Entry<String, Integer> additionalProfession : this.additionalProfessions.entrySet()) {
				if (additionalProfession != null) {
					NBTTagCompound nbtTagProfession = new NBTTagCompound();
					nbtTagProfession.setString(NBTTAG_VILLAGE_RESIDENTADDPROFNAME, additionalProfession.getKey());
					nbtTagProfession.setInteger(NBTTAG_VILLAGE_RESIDENTADDPROFLEVEL, additionalProfession.getValue());
					
					nbtTagListProfessions.appendTag(nbtTagProfession);
				}
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTADDPROF, nbtTagListProfessions);
		}
		
		if (this.aiFilters != null && this.aiFilters.size() > 0) {
			NBTTagList nbtTagListAIFilters = new NBTTagList();
			
			for (Entry<String, Boolean> aiFilter : this.aiFilters.entrySet()) {
				if (aiFilter != null) {
					NBTTagCompound nbtTagAIFilter = new NBTTagCompound();
					nbtTagAIFilter.setString(NBTTAG_VILLAGE_RESIDENTAIFILTERNAME, aiFilter.getKey());
					nbtTagAIFilter.setBoolean(NBTTAG_VILLAGE_RESIDENTAIFILTERENABLED, aiFilter.getValue());
					
					nbtTagListAIFilters.appendTag(nbtTagAIFilter);
				}
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTAIFILTER, nbtTagListAIFilters);
		}
		
		if (this.recipes != null && !this.recipes.isEmpty()) {
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTRECIPES, this.recipes.getRecipiesAsTags());
		}
		
		if (this.inventory != null && !this.inventory.isEmpty()) {
			NBTTagList nbtTagListInventory = new NBTTagList();
			
			for (ItemStack itemStack : this.inventory) {
				if (itemStack != null && !itemStack.isEmpty()) {					
					nbtTagListInventory.appendTag(itemStack.writeToNBT(new NBTTagCompound()));
				}
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTINVENTORY, nbtTagListInventory);
		}
		
		if (this.recentEats != null && !this.recentEats.isEmpty()) {
			NBTTagList nbtTagListRecentEats = new NBTTagList();
			
			for (Integer itemId : this.recentEats) {
				nbtTagListRecentEats.appendTag(new NBTTagInt(itemId));
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTRECENTEATS, nbtTagListRecentEats);
		}
		
		return nbtTag;
	}
	
}
