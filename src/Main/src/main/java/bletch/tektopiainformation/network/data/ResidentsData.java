package bletch.tektopiainformation.network.data;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import bletch.common.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ResidentsData {
	
	protected static final String NBTTAG_VILLAGE_RESIDENTS = "residents";
	protected static final String NBTTAG_VILLAGE_RESIDENTSLIST = "list";
	protected static final String NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNTS = "profs";
	protected static final String NBTTAG_VILLAGE_RESIDENTSPROFESSIONNAME = "name";
	protected static final String NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNT = "count";
	protected static final String NBTTAG_VILLAGE_RESIDENTADULTCOUNT = "adults";
	protected static final String NBTTAG_VILLAGE_RESIDENTCHILDCOUNT = "childs";
	protected static final String NBTTAG_VILLAGE_RESIDENTMALECOUNT = "males";
	protected static final String NBTTAG_VILLAGE_RESIDENTFEMALECOUNT = "females";
	
	public static final int STATISTICS_RANGE = 20;

	protected VillageData villageData;
	protected List<ResidentData> residents;
	protected Map<String, Integer> professionTypeCounts;

	protected int adultCount = 0;
	protected int childCount = 0;
	protected int maleCount = 0;
	protected int femaleCount = 0;

	public ResidentsData() {
		populateData(null, null);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getResidentsCount() {
		return (int)this.residents.stream()
				.filter(r -> !r.getProfessionType().equals(TektopiaUtils.PROFESSIONTYPE_ARCHITECT) && !r.getProfessionType().equals(TektopiaUtils.PROFESSIONTYPE_TRADESMAN))
				.count();
	}
	
	public int getResidentsCountAll() {
		return this.residents == null 
				? 0 
				: this.residents.size();
	}
	
	public List<ResidentData> getResidents() {
		return this.residents == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.residents.stream()
						.sorted(Comparator.comparing(EntityData::getName))
						.collect(Collectors.toList()));
	}
	
	public List<ResidentData> getResidentsByType(String professionType, Boolean sortByLevel) {
		return this.residents == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.residents.stream()
						.filter(r -> professionType != null && professionType.toUpperCase().equals(r.getProfessionType()))
						.sorted((c1 , c2) -> { 
							int compare = sortByLevel ? Integer.compare(c2.getBaseLevel(), c1.getBaseLevel()) : 0;
							return compare != 0 ? compare : c1.getName().compareTo(c2.getName());
						})
						.collect(Collectors.toList()));
	}
	
	public ResidentData getResident(int index) {
		return this.residents == null 
				? null 
				: this.residents.get(index);
	}
	
	public ResidentData getResidentById(int residentId) {
		return this.residents == null 
				? null 
				: this.residents.stream()
						.filter(r -> r.getId() == residentId)
						.findFirst().orElse(null);
	}
	
	public ResidentData getResidentByBedPosition(BlockPos bedPosition) {
		return this.residents == null 
				? null 
				: this.residents.stream()
						.filter(r -> bedPosition != null && bedPosition.equals(r.getBedPosition()))
						.findFirst().orElse(null);
	}
	
	public Map<String, Integer> getProfessionTypeCounts() {
		return this.professionTypeCounts == null 
				? Collections.unmodifiableMap(new LinkedHashMap<>())
				: Collections.unmodifiableMap(this.professionTypeCounts.entrySet().stream()
						.filter(e -> e.getValue() > 0 && !e.getKey().equals(TektopiaUtils.PROFESSIONTYPE_ARCHITECT) && !e.getKey().equals(TektopiaUtils.PROFESSIONTYPE_TRADESMAN))
						.sorted(Entry.comparingByKey())
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	public Map<String, Integer> getProfessionTypeCountsAll() {
		return this.professionTypeCounts == null 
				? Collections.unmodifiableMap(new LinkedHashMap<>())
				: Collections.unmodifiableMap(this.professionTypeCounts.entrySet().stream()
						.sorted(Entry.comparingByKey())
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}	
	
	public int getProfessionTypeCount(String professionType) {
		return professionType != null && this.professionTypeCounts != null && this.professionTypeCounts.containsKey(professionType.toUpperCase()) 
				? this.professionTypeCounts.get(professionType.toUpperCase()) 
				: 0;
	}
	
	public int getAdultCount() {
		return this.adultCount;
	}
	
	public int getChildCount() {
		return this.childCount;
	}
	
	public int getMaleCount() {
		return this.maleCount;
	}
	
	public int getFemaleCount() {
		return this.femaleCount;
	}
	
	public int getNoBedCount() {
		final int[] total = { 0 };
		if (this.residents != null) {
			this.residents.forEach(h -> total[0] += (!h.getCanHaveBed() || h.hasBed() ? 0 : 1));
		}
		return total[0];
	}
	
	public Map<Integer, List<ResidentData>> getResidentHappinessStatistics() {
		int rangeIndex = 0;

		Map<Integer, List<ResidentData>> result = new LinkedHashMap<>();
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		
		if (this.residents != null) {
			this.residents.forEach(r -> {
						int key = result.keySet().stream()
							.filter(k -> r.getHappy() <= k)
							.findFirst().orElse(-1);
						if (key > -1) {
							result.get(key).add(r);
						}
					});
		}
		
		for (Entry<Integer, List<ResidentData>> entry : result.entrySet()) {
			if (entry != null) {
				entry.getValue().sort(Comparator.comparingInt(ResidentData::getHappy).thenComparing(EntityData::getName));
			}
		}
		
		return result;
	}
	
	public Map<Integer, List<ResidentData>> getResidentHungerStatistics() {
		int rangeIndex = 0;

		Map<Integer, List<ResidentData>> result = new LinkedHashMap<>();
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<>());
		
		if (this.residents != null) {
			this.residents.forEach(r -> {
						int key = result.keySet().stream()
							.filter(k -> r.getHunger() <= k)
							.findFirst().orElse(-1);
						if (key > -1) {
							result.get(key).add(r);
						}
					});
		}
		
		for (Entry<Integer, List<ResidentData>> entry : result.entrySet()) {
			if (entry != null) {
				entry.getValue().sort(Comparator.comparingInt(ResidentData::getHunger).thenComparing(EntityData::getName));
			}
		}
		
		return result;
	}
	
	public ResidentData getArchitect() {
		return this.residents == null 
				? null 
				: this.residents.stream()
						.filter(r -> r.getProfessionType().equals(TektopiaUtils.PROFESSIONTYPE_ARCHITECT))
						.findFirst().orElse(null);
	}
	
	public ResidentData getTradesman() {
		return this.residents == null 
				? null 
				: this.residents.stream()
						.filter(r -> r.getProfessionType().equals(TektopiaUtils.PROFESSIONTYPE_TRADESMAN))
						.findFirst().orElse(null);
	}
	
	protected void clearData() {
		this.residents = new ArrayList<>();
		this.professionTypeCounts = new HashMap<>();

		this.adultCount = 0;
		this.childCount = 0;
		this.maleCount = 0;
		this.femaleCount = 0;
	}
	
	public void populateData(VillageData villageData, Village village) {
		clearData();
		
		this.villageData = villageData;
		
		if (village != null) {
			
			// get the resident data
			List<EntityVillagerTek> villageResidents = TektopiaUtils.getVillageResidents(village);

			for (String professionType : TektopiaUtils.getProfessionTypeNames(true)) {
				this.professionTypeCounts.put(professionType, 0);
			}
			
			for (EntityVillagerTek resident : villageResidents) {
				if (resident.isDead) {
					continue;
				}
				
				this.residents.add(new ResidentData(resident));
				
				ProfessionType professionType = resident.getProfessionType();
				if (resident instanceof EntityGuard && ((EntityGuard)resident).isCaptain()) {
					professionType = ProfessionType.CAPTAIN;
				}
				this.professionTypeCounts.put(professionType.name(), this.professionTypeCounts.get(professionType.name()) + 1);

				if (professionType == ProfessionType.CHILD)
					this.childCount++;
				else
					this.adultCount++;
				
				if (resident.isMale())
					this.maleCount++;
				else
					this.femaleCount++;
			}
			
			AxisAlignedBB villageAABB = village.getAABB().grow(Village.VILLAGE_SIZE);
			
			// populate Architect
			List<EntityVillagerTek> villageArchitects = village.getWorld().getEntitiesWithinAABB(EntityArchitect.class, villageAABB);
			
			for (EntityVillagerTek entity : villageArchitects) {
				if (entity.isDead) {
					continue;
				}
				
				this.professionTypeCounts.put(TektopiaUtils.PROFESSIONTYPE_ARCHITECT, this.professionTypeCounts.get(TektopiaUtils.PROFESSIONTYPE_ARCHITECT) + 1);
				this.residents.add(new ResidentData(entity));

				this.adultCount++;
				
				if (entity.isMale())
					this.maleCount++;
				else
					this.femaleCount++;
			}
			
			// populate Tradesman
			List<EntityVillagerTek> villageTradesmen = village.getWorld().getEntitiesWithinAABB(EntityTradesman.class, villageAABB);
			
			for (EntityVillagerTek entity : villageTradesmen) {
				if (entity.isDead) {
					continue;
				}
				
				this.professionTypeCounts.put(TektopiaUtils.PROFESSIONTYPE_TRADESMAN, this.professionTypeCounts.get(TektopiaUtils.PROFESSIONTYPE_TRADESMAN) + 1);
				this.residents.add(new ResidentData(entity));

				this.adultCount++;
				
				if (entity.isMale())
					this.maleCount++;
				else
					this.femaleCount++;
			}
		}
	}
	
	public void readNBT(VillageData villageData, NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.villageData = villageData;
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTS)) {
			NBTTagCompound nbtResidentsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_RESIDENTS);
		
			if (nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTSLIST)) {
				NBTTagList nbtTagListResidents = nbtResidentsData.getTagList(NBTTAG_VILLAGE_RESIDENTSLIST, 10);
				
				for (int index = 0; index < nbtTagListResidents.tagCount(); index++) {
					this.residents.add(new ResidentData(nbtTagListResidents.getCompoundTagAt(index)));
				}
			}
			
			if (nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNTS)) {
				NBTTagList nbtTagProfessionCounts = nbtResidentsData.getTagList(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNTS, 10);	
				
				for (int index = 0; index < nbtTagProfessionCounts.tagCount(); index++) {
					NBTTagCompound nbtTagProfessionCount = nbtTagProfessionCounts.getCompoundTagAt(index);
					
					this.professionTypeCounts.put(nbtTagProfessionCount.getString(NBTTAG_VILLAGE_RESIDENTSPROFESSIONNAME), nbtTagProfessionCount.getInteger(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNT));
				}
			}
		
			this.adultCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTADULTCOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTADULTCOUNT) : 0;
			this.childCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT) : 0;
			this.maleCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTMALECOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTMALECOUNT) : 0;
			this.femaleCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT) : 0;
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtResidentsData = new NBTTagCompound();

		if (this.professionTypeCounts != null && this.professionTypeCounts.size() > 0) {
			NBTTagList nbtTagListProfessionCounts = new NBTTagList();
			
			for (Entry<String, Integer> professionType : this.professionTypeCounts.entrySet()) {
				NBTTagCompound nbtTagProfessionCount = new NBTTagCompound();
				nbtTagProfessionCount.setString(NBTTAG_VILLAGE_RESIDENTSPROFESSIONNAME, professionType.getKey());
				nbtTagProfessionCount.setInteger(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNT, professionType.getValue());
				
				nbtTagListProfessionCounts.appendTag(nbtTagProfessionCount);
			}
			
			if (!nbtTagListProfessionCounts.hasNoTags()) {
				nbtResidentsData.setTag(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNTS, nbtTagListProfessionCounts);
			}
		}
		
		if (this.residents != null && this.residents.size() > 0) {
			NBTTagList nbtTagListResidents = new NBTTagList();
			
			for (ResidentData resident : this.residents) {
				NBTTagCompound nbtTagResident = resident.writeNBT(new NBTTagCompound());
				if (!nbtTagResident.hasNoTags()) {
					nbtTagListResidents.appendTag(nbtTagResident);
				}
			}
			
			if (!nbtTagListResidents.hasNoTags()) {
				nbtResidentsData.setTag(NBTTAG_VILLAGE_RESIDENTSLIST, nbtTagListResidents);
			}
		}
		
		if (this.adultCount > 0) {
			nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTADULTCOUNT, this.adultCount);
		}
		if (this.childCount > 0) {
			nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT, this.childCount);
		}
		if (this.maleCount > 0) {
			nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTMALECOUNT, this.maleCount);
		}
		if (this.femaleCount > 0) {
			nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT, this.femaleCount);
		}

		if (!nbtResidentsData.hasNoTags()) {
			nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTS, nbtResidentsData);
		}
		
		return nbtTag;
	}
	
}
