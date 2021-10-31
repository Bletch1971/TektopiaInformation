package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import bletch.tektopiainformation.utils.TektopiaUtils;
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
	
	private static final String NBTTAG_VILLAGE_RESIDENTS = "villageresidents";
	private static final String NBTTAG_VILLAGE_RESIDENTSLIST = "villageresidentslist";
	private static final String NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNTS = "villageresidentsprofessioncounts";
	private static final String NBTTAG_VILLAGE_RESIDENTSPROFESSIONNAME = "villageresidentsprofessionname";
	private static final String NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNT = "villageresidentsprofessioncount";
	private static final String NBTTAG_VILLAGE_RESIDENTADULTCOUNT = "villageresidentadultcount";
	private static final String NBTTAG_VILLAGE_RESIDENTCHILDCOUNT = "villageresidentchildcount";
	private static final String NBTTAG_VILLAGE_RESIDENTMALECOUNT = "villageresidentmalecount";
	private static final String NBTTAG_VILLAGE_RESIDENTFEMALECOUNT = "villageresidentfemalecount";
	
	public static final int STATISTICS_RANGE = 20;

	private List<ResidentData> residents;
	private Map<String, Integer> professionTypeCounts;

	private int adultCount = 0;
	private int childCount = 0;
	private int maleCount = 0;
	private int femaleCount = 0;

	public ResidentsData() {
		populateData(null);
	}
	
	public ResidentsData(Village village) {
		populateData(village);
	}
	
	public int getResidentsCount() {
		return this.residents.size();
	}
	
	public int getResidentsCountAll() {
		return this.residents == null 
				? 0 
				: this.residents.size();
	}
	
	public List<ResidentData> getResidents() {
		return this.residents == null
				? Collections.unmodifiableList(new ArrayList<ResidentData>())
				: Collections.unmodifiableList(this.residents.stream()
						.sorted((c1 , c2) -> c1.getName().compareTo(c2.getName()))
						.collect(Collectors.toList()));
	}
	
	public List<ResidentData> getResidentsByType(String professionType) {
		return this.residents == null
				? Collections.unmodifiableList(new ArrayList<ResidentData>())
				: Collections.unmodifiableList(this.residents.stream()
						.filter(r -> professionType != null && professionType.toUpperCase().equals(r.getProfessionType()))
						.sorted((c1 , c2) -> { 
							int compare = Integer.compare(c2.getBaseLevel(), c1.getBaseLevel());
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
	
	public Map<String, Integer> getAllProfessionTypeCounts() {
		return this.professionTypeCounts == null 
				? Collections.unmodifiableMap(new LinkedHashMap<String, Integer>())
				: Collections.unmodifiableMap(this.professionTypeCounts.entrySet().stream()
						.sorted((c1 , c2) -> c1.getKey().compareTo(c2.getKey()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	public Map<String, Integer> getProfessionTypeCounts() {
		return this.professionTypeCounts == null 
				? Collections.unmodifiableMap(new LinkedHashMap<String, Integer>())
				: Collections.unmodifiableMap(this.professionTypeCounts.entrySet().stream()
						.filter(e -> e.getValue() > 0 && !e.getKey().equals(TektopiaUtils.PROFESSIONTYPE_ARCHITECT) && !e.getKey().equals(TektopiaUtils.PROFESSIONTYPE_TRADESMAN))
						.sorted((c1 , c2) -> c1.getKey().compareTo(c2.getKey()))
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
			this.residents.stream()
					.forEach(h -> total[0] += (!h.getCanHaveBed() || h.hasBed() ? 0 : 1));
		}
		return total[0];
	}
	
	public Map<Integer, List<ResidentData>> getResidentHappinessStatistics() {
		int rangeIndex = 0;

		Map<Integer, List<ResidentData>> result = new LinkedHashMap<Integer, List<ResidentData>>();
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		
		if (this.residents != null) {
			this.residents.stream()
					.forEach(r -> {
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
				entry.getValue().sort((c1, c2) -> {
					int compare = Integer.compare(c1.getHappy(), c2.getHappy());
					return compare != 0 ? compare : c1.getName().compareTo(c2.getName());
				});
			}
		}
		
		return result;
	}
	
	public Map<Integer, List<ResidentData>> getResidentHungerStatistics() {
		int rangeIndex = 0;

		Map<Integer, List<ResidentData>> result = new LinkedHashMap<Integer, List<ResidentData>>();
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		
		if (this.residents != null) {
			this.residents.stream()
					.forEach(r -> {
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
				entry.getValue().sort((c1, c2) -> {
					int compare = Integer.compare(c1.getHunger(), c2.getHunger());
					return compare != 0 ? compare : c1.getName().compareTo(c2.getName());
				});
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
	
	private void clearData() {
		this.residents = new ArrayList<ResidentData>();
		this.professionTypeCounts = new HashMap<String, Integer>();

		this.adultCount = 0;
		this.childCount = 0;
		this.maleCount = 0;
		this.femaleCount = 0;
	}
	
	public void populateData(Village village) {
		clearData();
		
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
			}
			
			// populate Tradesman
			List<EntityVillagerTek> villageTradesmen = village.getWorld().getEntitiesWithinAABB(EntityTradesman.class, villageAABB);
			
			for (EntityVillagerTek entity : villageTradesmen) {
				if (entity.isDead) {
					continue;
				}
				
				this.professionTypeCounts.put(TektopiaUtils.PROFESSIONTYPE_TRADESMAN, this.professionTypeCounts.get(TektopiaUtils.PROFESSIONTYPE_TRADESMAN) + 1);
				this.residents.add(new ResidentData(entity));
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTS)) {
			NBTTagCompound nbtResidentsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_RESIDENTS);
		
			if (nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTSLIST)) {
				NBTTagList nbtTagListResidents = nbtResidentsData.getTagList(NBTTAG_VILLAGE_RESIDENTSLIST, 10);
				
				for (int index = 0; index < nbtTagListResidents.tagCount(); index++) {
					NBTTagCompound nbtTagResident = nbtTagListResidents.getCompoundTagAt(index);
					
					this.residents.add(new ResidentData(nbtTagResident));
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
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtResidentsData = new NBTTagCompound();
		
		if (this.residents != null) {
			NBTTagList nbtTagListResidents = new NBTTagList();
			
			for (ResidentData resident : this.residents) {
				NBTTagCompound nbtResident = new NBTTagCompound();
				resident.writeNBT(nbtResident);
				
				nbtTagListResidents.appendTag(nbtResident);
			}
			
			nbtResidentsData.setTag(NBTTAG_VILLAGE_RESIDENTSLIST, nbtTagListResidents);
		}

		if (this.professionTypeCounts != null) {
			NBTTagList nbtTagListProfessionCounts = new NBTTagList();
			
			for (Entry<String, Integer> professionType : this.professionTypeCounts.entrySet()) {
				NBTTagCompound nbtTagProfessionCount = new NBTTagCompound();
				nbtTagProfessionCount.setString(NBTTAG_VILLAGE_RESIDENTSPROFESSIONNAME, professionType.getKey());
				nbtTagProfessionCount.setInteger(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNT, professionType.getValue());
				
				nbtTagListProfessionCounts.appendTag(nbtTagProfessionCount);
			}
			
			nbtResidentsData.setTag(NBTTAG_VILLAGE_RESIDENTSPROFESSIONCOUNTS, nbtTagListProfessionCounts);
		}
		
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTADULTCOUNT, this.adultCount);
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT, this.childCount);
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTMALECOUNT, this.maleCount);
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT, this.femaleCount);

		nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTS, nbtResidentsData);
	}
	
}
