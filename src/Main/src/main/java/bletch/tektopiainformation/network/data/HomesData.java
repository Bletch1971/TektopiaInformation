package bletch.tektopiainformation.network.data;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class HomesData {
	
	protected static final String NBTTAG_VILLAGE_HOMES = "villagehomes";
	protected static final String NBTTAG_VILLAGE_HOMESLIST = "villagehomeslist";
	protected static final String NBTTAG_VILLAGE_HOMETYPECOUNTS = "villagehometypecounts";
	protected static final String NBTTAG_VILLAGE_HOMETYPENAME = "villagehometypename";
	protected static final String NBTTAG_VILLAGE_HOMETYPECOUNT = "villagehometypecount";

	protected VillageData villageData;
	protected List<HomeData> homes;
	protected Map<VillageStructureType, Integer> homeTypeCounts;
	
	public HomesData() {
		populateData(null, null);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getHomesCount() {
		return this.homes == null 
				? 0 
				: this.homes.size();
	}
	
	public List<HomeData> getHomes() {
		return this.homes == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.homes.stream()
						.sorted(Comparator.comparing((HomeData c) -> c.getStructureType().name()).thenComparing(HomeData::getFramePosition))
						.collect(Collectors.toList()));
	}
	
	public List<HomeData> getHomesByType(VillageStructureType structureType) {
		return this.homes == null 
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.homes.stream()
						.filter(h -> h.getStructureType().equals(structureType))
						.sorted(Comparator.comparing(HomeData::getFramePosition))
						.collect(Collectors.toList()));
	}
	
	public HomeData getHome(int index) {
		return this.homes == null 
				? null 
				: this.homes.get(index);
	}
	
	public HomeData getHomeById(int homeId) {
		return this.homes == null 
				? null 
				: this.homes.stream()
						.filter(r -> homeId > 0 && homeId == r.getHomeId())
						.findFirst().orElse(null);
	}
	
	public HomeData getHomeByBedPosition(BlockPos bedPosition) {
		return this.homes == null 
				? null 
				: this.homes.stream()
						.filter(h -> bedPosition != null && h.getBedPositions().contains(bedPosition))
						.findFirst().orElse(null);
	}
	
	public HomeData getHomeByFramePosition(BlockPos framePosition) {
		return this.homes == null 
				? null 
				: this.homes.stream()
						.filter(h -> framePosition != null && framePosition.equals(h.getFramePosition()))
						.findFirst().orElse(null);
	}
	
	public Map<VillageStructureType, Integer> getHomeTypeCounts() {
		return this.homeTypeCounts == null 
				? Collections.unmodifiableMap(new LinkedHashMap<>())
				: Collections.unmodifiableMap(this.homeTypeCounts.entrySet().stream()
						.sorted(Comparator.comparing(c -> c.getKey().name()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}	
	
	public int getHomeTypeCount(VillageStructureType structureType) {
		return this.homeTypeCounts != null && structureType != null && this.homeTypeCounts.containsKey(structureType) 
				? this.homeTypeCounts.get(structureType) 
				: 0;
	}
	
	public int getResidentCountByType(VillageStructureType structureType) {
		final int[] total = { 0 };
		if (this.homes != null) {
			this.homes.stream()
					.filter(h -> structureType != null && structureType.equals(h.getStructureType()))
					.forEach(h -> total[0] += h.getResidentsCount());
		}
		return total[0];
	}
	
	public int getMaxBeds() {
		final int[] total = { 0 };
		if (this.homes != null) {
			this.homes.forEach(h -> total[0] += h.getMaxBeds());
		}
		return total[0];
	}
	
	public int getTotalBeds() {
		final int[] total = { 0 };
		if (this.homes != null) {
			this.homes.forEach(h -> total[0] += h.getBedCount());
		}
		return total[0];
	}
	
	protected void clearData() {
		this.homes = new ArrayList<>();
		this.homeTypeCounts = new LinkedHashMap<>();
	}
	
	public void populateData(VillageData villageData, Village village) {
		clearData();
		
		this.villageData = villageData;
		
		if (village != null) {
			
			Map<VillageStructureType, List<VillageStructure>> homesList = TektopiaUtils.getVillageHomes(village);
			
			for (Entry<VillageStructureType, List<VillageStructure>> entry : homesList.entrySet()) {
				List<VillageStructure> structures = homesList.get(entry.getKey());
				this.homeTypeCounts.put(entry.getKey(), structures.size());

				for (VillageStructure structure : structures) {
					this.homes.add(new HomeData(villageData, structure));
				}
			}
		}
	}
	
	public void readNBT(VillageData villageData, NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.villageData = villageData;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_HOMES)) {
			NBTTagCompound nbtHomesData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_HOMES);
			
			if (nbtHomesData.hasKey(NBTTAG_VILLAGE_HOMETYPECOUNTS)) {
				NBTTagList nbtTagHomeCounts = nbtHomesData.getTagList(NBTTAG_VILLAGE_HOMETYPECOUNTS, 10);
				
				for (int index = 0; index < nbtTagHomeCounts.tagCount(); index++) {
					NBTTagCompound nbtTagHomeCount = nbtTagHomeCounts.getCompoundTagAt(index);
					VillageStructureType homeType = VillageStructureType.valueOf(nbtTagHomeCount.getString(NBTTAG_VILLAGE_HOMETYPENAME));
					
					this.homeTypeCounts.put(homeType, nbtTagHomeCount.getInteger(NBTTAG_VILLAGE_HOMETYPECOUNT));
				}	
			}
			
			if (nbtHomesData.hasKey(NBTTAG_VILLAGE_HOMESLIST)) {
				NBTTagList nbtTagListHomes = nbtHomesData.getTagList(NBTTAG_VILLAGE_HOMESLIST, 10);
				
				for (int index = 0; index < nbtTagListHomes.tagCount(); index++) {
					this.homes.add(new HomeData(villageData, nbtTagListHomes.getCompoundTagAt(index)));
				}
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtHomesData = new NBTTagCompound();

		if (this.homeTypeCounts != null) {
			NBTTagList nbtTagListHomeCounts = new NBTTagList();
			
			for (Entry<VillageStructureType, Integer> homeType : this.homeTypeCounts.entrySet()) {
				NBTTagCompound nbtTagHomeCount = new NBTTagCompound();
				nbtTagHomeCount.setString(NBTTAG_VILLAGE_HOMETYPENAME, homeType.getKey().name());
				nbtTagHomeCount.setInteger(NBTTAG_VILLAGE_HOMETYPECOUNT, homeType.getValue());
				
				nbtTagListHomeCounts.appendTag(nbtTagHomeCount);
			}
			
			nbtHomesData.setTag(NBTTAG_VILLAGE_HOMETYPECOUNTS, nbtTagListHomeCounts);
		}
		
		if (this.homes != null) {
			NBTTagList nbtTagListHomes = new NBTTagList();

			for (HomeData structure : this.homes) {
				nbtTagListHomes.appendTag(structure.writeNBT(new NBTTagCompound()));
			}
			
			nbtHomesData.setTag(NBTTAG_VILLAGE_HOMESLIST, nbtTagListHomes);
		}
		
		nbtTag.setTag(NBTTAG_VILLAGE_HOMES, nbtHomesData);
		
		return nbtTag;
	}
	
}
