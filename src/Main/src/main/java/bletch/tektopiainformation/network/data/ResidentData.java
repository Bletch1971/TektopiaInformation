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
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
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

	private static final String NBTTAG_VILLAGE_RESIDENTPROFESSIONTYPE = "villageresidentprofessiontype";
	private static final String NBTTAG_VILLAGE_RESIDENTMALE = "villageresidentmale";
	private static final String NBTTAG_VILLAGE_RESIDENTCHILD = "villageresidentchild";
	private static final String NBTTAG_VILLAGE_RESIDENTCAPTAIN = "villageresidentcaptain";
	private static final String NBTTAG_VILLAGE_RESIDENTVENDOR = "villageresidentvendor";
	private static final String NBTTAG_VILLAGE_RESIDENTVISITOR = "villageresidentvisitor";
	private static final String NBTTAG_VILLAGE_RESIDENTSLEEPING = "villageresidentsleeping";
	private static final String NBTTAG_VILLAGE_RESIDENTBASELEVEL = "villageresidentbaselevel";
	private static final String NBTTAG_VILLAGE_RESIDENTBLESSEDLEVEL = "villageresidentblessedlevel";
	private static final String NBTTAG_VILLAGE_RESIDENTDAYSALIVE = "villageresidentdaysAlive";
	private static final String NBTTAG_VILLAGE_RESIDENTHUNGER = "villageresidenthunger";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXHUNGER = "villageresidentmaxhunger";
	private static final String NBTTAG_VILLAGE_RESIDENTHAPPY = "villageresidenthappy";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXHAPPY = "villageresidentmaxhappy";
	private static final String NBTTAG_VILLAGE_RESIDENTINTELLIGENCE = "villageresidentintellegience";
	private static final String NBTTAG_VILLAGE_RESIDENTMAXINTELLIGENCE = "villageresidentmaxintellegience";

	private static final String NBTTAG_VILLAGE_RESIDENTWORKSTART = "villageresidentworkstart";
	private static final String NBTTAG_VILLAGE_RESIDENTWORKFINISH = "villageresidentworkfinish";
	private static final String NBTTAG_VILLAGE_RESIDENTSLEEPSTART = "villageresidentsleepstart";
	private static final String NBTTAG_VILLAGE_RESIDENTSLEEPFINISH = "villageresidentsleepfinish";
	
	private static final String NBTTAG_VILLAGE_RESIDENTCANHAVEBED = "villageresidentcanhavebed";
	private static final String NBTTAG_VILLAGE_RESIDENTBEDPOSITION = "villageresidentbedposition";
	private static final String NBTTAG_VILLAGE_RESIDENTCURRENTSTRUCTURE = "villageresidentcurrentstructure";
	private static final String NBTTAG_VILLAGE_RESIDENTCURRENTTASK = "villageresidentcurrenttask";
	private static final String NBTTAG_VILLAGE_RESIDENTADDPROFCOUNT = "villageresidentaddprofcount";
	private static final String NBTTAG_VILLAGE_RESIDENTADDPROF = "villageresidentaddprof";
	private static final String NBTTAG_VILLAGE_RESIDENTAIFILTERCOUNT = "villageresidentaifiltercount";
	private static final String NBTTAG_VILLAGE_RESIDENTAIFILTER = "villageresidentaifilter";
	private static final String NBTTAG_VILLAGE_RESIDENTRECIPES = "villageresidentrecipes";
	private static final String NBTTAG_VILLAGE_RESIDENTINVENTORYCOUNT = "villageresidentinventorycount";
	private static final String NBTTAG_VILLAGE_RESIDENTINVENTORY = "villageresidentinventory";

	@SuppressWarnings("rawtypes")
	private static final List<Class> toolItemClasses = Arrays.asList(ItemAxe.class, ItemHoe.class, ItemSword.class, ItemPickaxe.class, ItemShears.class);
	
	protected String professionType;
	private boolean isMale;
	private boolean isChild;
	private boolean isCaptain;
	private boolean isVendor;
	private boolean isVisitor;
	private boolean isSleeping;
	private int baseLevel;
	private int blessedLevel;
	private int daysAlive;
	private int hunger;
	private int maxHunger;
	private int happy;
	private int maxHappy;
	private int intelligence;
	private int maxIntelligence;
	private int workStartTime;
	private int workFinishTime;
	private int sleepStartTime;
	private int sleepFinishTime;
	
	protected Boolean canHaveBed;
	private BlockPos bedPosition;
	private BlockPos currentStructure;
	private String currentTask;
	
	private Map<String, Integer> additionalProfessions;
	private Map<String, Boolean> aiFilters;
	private MerchantRecipeList recipes;
	private List<ItemStack> inventory;
	
	public ResidentData() {
		super();
	}
	
	public ResidentData(EntityVillagerTek villager) {
		super(villager, false);
		
		populateData(villager);
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
		return this.additionalProfessions == null ? 0 : this.additionalProfessions.size();
	}
	
	public Map<String, Boolean> getAiFilters() {
		return this.aiFilters == null 
				? Collections.unmodifiableMap(new LinkedHashMap<String, Boolean>())
				: Collections.unmodifiableMap(this.aiFilters.entrySet().stream()
					.sorted((c1 , c2) -> c1.getKey().compareTo(c2.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	public int getAiFiltersCount() {
		return this.aiFilters == null ? 0 : this.aiFilters.size();
	}
	
	public MerchantRecipeList getRecipeList() {
		return this.recipes;
	}
	
	public List<ItemStack> getInventory() {
		return this.inventory;
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
			
			for (String professionType : TektopiaUtils.getProfessionTypeNames()) {
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
			this.equipment.removeIf((p) -> p == null || p == ItemStack.EMPTY || p.getItem() == Items.AIR);
			
			int inventorySize = inventory.getSizeInventory();
			if (inventorySize > 0) {
				this.inventory = new ArrayList<ItemStack>(inventorySize);
				
				for (int slot = 0; slot < inventorySize; slot++) {
					this.inventory.add(inventory.getStackInSlot(slot).copy());
				}
			}
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
								this.additionalProfessions.put(professionType.name(), level);
							}
							catch (Exception ex) {
								// do nothing if conversion error
							}
						}
					}
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTAIFILTERCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTAIFILTERCOUNT);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_RESIDENTAIFILTER + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					String value = nbtTag.getString(key);
					
					if (value != null && value.contains("@")) {
						String[] valueParts = value.split("@");
						
						if (valueParts.length == 2) {
							try {
								Boolean enabled = Boolean.parseBoolean(valueParts[1]);
								this.aiFilters.put(valueParts[0], enabled);
							}
							catch (Exception ex) {
								// do nothing if conversion error
							}
						}
					}
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTRECIPES)) {
			if (this.recipes == null)
				this.recipes = new MerchantRecipeList();
			this.recipes.readRecipiesFromTags(nbtTag.getCompoundTag(NBTTAG_VILLAGE_RESIDENTRECIPES));
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTINVENTORYCOUNT)) {
			int count = nbtTag.getInteger(NBTTAG_VILLAGE_RESIDENTINVENTORYCOUNT);			
			this.inventory = new ArrayList<ItemStack>(count);
			
			for (int index = 0; index < count; index++) {
				String key = NBTTAG_VILLAGE_RESIDENTINVENTORY + "@" + index;
				
				if (nbtTag.hasKey(key)) {
					NBTTagCompound value = nbtTag.getCompoundTag(key);
					ItemStack itemStack = new ItemStack(value);
					
					this.inventory.add(itemStack);
				}
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
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		super.writeNBT(nbtTag);
		
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
			int index = 0;
			for (Entry<String, Integer> additionalProfession : this.additionalProfessions.entrySet()) {
				if (additionalProfession != null) {
					nbtTag.setString(NBTTAG_VILLAGE_RESIDENTADDPROF + "@" + index, additionalProfession.getKey() + "@" + additionalProfession.getValue());
					index++;
				}
			}
			
			nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTADDPROFCOUNT, index);
		}
		
		if (this.aiFilters != null && this.aiFilters.size() > 0) {
			int index = 0;
			for (Entry<String, Boolean> aiFilter : this.aiFilters.entrySet()) {
				if (aiFilter != null) {
					nbtTag.setString(NBTTAG_VILLAGE_RESIDENTAIFILTER + "@" + index, aiFilter.getKey() + "@" + aiFilter.getValue());
					index++;
				}
			}
			
			nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTAIFILTERCOUNT, index);
		}
		
		if (this.recipes != null && !this.recipes.isEmpty()) {
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTRECIPES, this.recipes.getRecipiesAsTags());
		}
		
		if (this.inventory != null && !this.inventory.isEmpty()) {
			nbtTag.setInteger(NBTTAG_VILLAGE_RESIDENTINVENTORYCOUNT, this.inventory.size());
			
			int index = 0;
			for (ItemStack itemStack : this.inventory) {
				if (itemStack != null) {
					NBTTagCompound value = new NBTTagCompound();
					value = itemStack.writeToNBT(value);
					
					nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTINVENTORY + "@" + index, value);
				}
				index++;
			}
		}
	}
	
}
