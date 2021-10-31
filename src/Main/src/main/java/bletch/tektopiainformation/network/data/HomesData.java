package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	private static final String NBTTAG_VILLAGE_HOMES = "villagehomes";
	private static final String NBTTAG_VILLAGE_HOMESLIST = "villagehomeslist";
	private static final String NBTTAG_VILLAGE_HOMETYPECOUNTS = "villagehometypecounts";
	private static final String NBTTAG_VILLAGE_HOMETYPENAME = "villagehometypename";
	private static final String NBTTAG_VILLAGE_HOMETYPECOUNT = "villagehometypecount";
	
	private List<HomeData> homes;
	private Map<VillageStructureType, Integer> homeTypeCounts;
	
	public HomesData() {
		populateData(null);
	}
	
	public HomesData(Village village) {
		populateData(village);
	}
	
	public int getHomesCount() {
		return this.homes == null 
				? 0 
				: this.homes.size();
	}
	
	public List<HomeData> getHomes() {
		return this.homes == null
				? Collections.unmodifiableList(new ArrayList<HomeData>())
				: Collections.unmodifiableList(this.homes.stream()
						.sorted((c1 , c2) -> {
							int compare = c1.getStructureType().name().compareTo(c2.getStructureType().name());
							return compare != 0 ? compare : c1.getFramePosition().compareTo(c2.getFramePosition());
						})
						.collect(Collectors.toList()));
	}
	
	public List<HomeData> getHomesByType(VillageStructureType structureType) {
		return this.homes == null 
				? Collections.unmodifiableList(new ArrayList<HomeData>())
				: Collections.unmodifiableList(this.homes.stream()
						.filter(h -> structureType != null && h.getStructureType().equals(structureType))
						.sorted((c1 , c2) -> c1.getFramePosition().compareTo(c2.getFramePosition()))
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
				? Collections.unmodifiableMap(new HashMap<VillageStructureType, Integer>())
				: Collections.unmodifiableMap(this.homeTypeCounts);
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
			this.homes.stream()
					.forEach(h -> total[0] += h.getMaxBeds());
		}
		return total[0];
	}
	
	public int getTotalBeds() {
		final int[] total = { 0 };
		if (this.homes != null) {
			this.homes.stream()
					.forEach(h -> total[0] += h.getBedCount());
		}
		return total[0];
	}
	
	private void clearData() {
		this.homes = new ArrayList<HomeData>();
		this.homeTypeCounts = new HashMap<VillageStructureType, Integer>();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			
			Map<VillageStructureType, List<VillageStructure>> homesList = TektopiaUtils.getVillageHomes(village);
			
			for (Entry<VillageStructureType, List<VillageStructure>> entry : homesList.entrySet()) {
				List<VillageStructure> structures = homesList.get(entry.getKey());
				this.homeTypeCounts.put(entry.getKey(), structures.size());

				for (VillageStructure structure : structures) {
					this.homes.add(new HomeData(structure));
				}
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();

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
					NBTTagCompound nbtTagHome = nbtTagListHomes.getCompoundTagAt(index);
					
					this.homes.add(new HomeData(nbtTagHome));
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
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
				NBTTagCompound nbtHome = new NBTTagCompound();
				structure.writeNBT(nbtHome);
				
				nbtTagListHomes.appendTag(nbtHome);
			}
			
			nbtHomesData.setTag(NBTTAG_VILLAGE_HOMESLIST, nbtTagListHomes);
		}
		
		nbtTag.setTag(NBTTAG_VILLAGE_HOMES, nbtHomesData);
	}
	
}
