package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.UUID;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class HomesData {
	
	private static final String NBTTAG_VILLAGE_HOMES = "villagehomes";
	private static final String NBTTAG_VILLAGE_HOMESCOUNT = "villagehomescount";
	
	private int homesCount;
	private List<HomeData> homes;
	private Map<VillageStructureType, Integer> homeTypeCounts;
	
	public HomesData() {
		populateData(null);
	}
	
	public HomesData(Village village) {
		populateData(village);
	}
	
	public int getHomesCount() {
		return this.homesCount;
	}
	
	public List<HomeData> getHomes() {
		return Collections.unmodifiableList(this.homes == null ? new ArrayList<HomeData>() : this.homes.stream()
				.sorted((c1 , c2) -> c1.getStructureType().name().compareTo(c2.getStructureType().name()))
				.collect(Collectors.toList()));
	}
	
	public List<HomeData> getHomesByType(VillageStructureType structureType) {
		return this.homes == null ? new ArrayList<HomeData>() :  Collections.unmodifiableList(this.homes.stream()
				.filter(h -> structureType != null && h.getStructureType().equals(structureType))
				.sorted((c1 , c2) -> c1.getFramePosition().compareTo(c2.getFramePosition()))
				.collect(Collectors.toList()));
	}
	
	public HomeData getHome(int index) {
		return this.homes == null ? null : this.homes.get(index);
	}
	
	public HomeData getHomeById(UUID homeId) {
		return this.homes == null ? null : this.homes.stream()
				.filter(r -> homeId != null && homeId.equals(r.getHomeId()))
				.findFirst().orElse(null);
	}
	
	public HomeData getHomeByBedPosition(BlockPos bedPosition) {
		return this.homes == null ? null : this.homes.stream()
				.filter(h -> bedPosition != null && h.getBedPositions().contains(bedPosition))
				.findFirst().orElse(null);
	}
	
	public HomeData getHomeByFramePosition(BlockPos framePosition) {
		return this.homes == null ? null : this.homes.stream()
				.filter(h -> framePosition != null && framePosition.equals(h.getFramePosition()))
				.findFirst().orElse(null);
	}
	
	public Map<VillageStructureType, Integer> getHomeTypeCounts() {
		return this.homeTypeCounts == null ? new HashMap<VillageStructureType, Integer>() : Collections.unmodifiableMap(this.homeTypeCounts);
	}	
	
	public int getHomeTypeCount(VillageStructureType structureType) {
		return this.homeTypeCounts != null && structureType != null && this.homeTypeCounts.containsKey(structureType) ? this.homeTypeCounts.get(structureType) : 0;
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
		this.homesCount = 0;
		
		this.homes = new ArrayList<HomeData>();
		this.homeTypeCounts = new HashMap<VillageStructureType, Integer>();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {

			Map<VillageStructureType, List<VillageStructure>> structuresList = TektopiaUtils.getVillageStructures(village); 
			List<VillageStructureType> homeTypes = TektopiaUtils.getHomeStructureTypes();
			
			final int[] totalHomeCount = { 0 };
			structuresList.entrySet().stream()
				.filter(s -> homeTypes != null && homeTypes.contains(s.getKey()))
				.forEach(s -> totalHomeCount[0] += s.getValue().size());

			this.homesCount = totalHomeCount[0];
			
			for (VillageStructureType structureType : TektopiaUtils.getVillageStructureTypes()) {
				if (homeTypes != null && homeTypes.contains(structureType)) {
					this.homeTypeCounts.put(structureType, 0);
					
					if (structuresList.containsKey(structureType)) {
						List<VillageStructure> structures = structuresList.get(structureType);
						this.homeTypeCounts.put(structureType, structures.size());

						for (VillageStructure structure : structures) {
							this.homes.add(new HomeData(structure));
						}
					}
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
			
			this.homesCount = nbtHomesData.hasKey(NBTTAG_VILLAGE_HOMESCOUNT) ? nbtHomesData.getInteger(NBTTAG_VILLAGE_HOMESCOUNT) : 0;
			
			List<VillageStructureType> homeTypes = TektopiaUtils.getHomeStructureTypes();

			for (VillageStructureType structureType : TektopiaUtils.getVillageStructureTypes()) {
				if (homeTypes != null && homeTypes.contains(structureType)) {
					String key = getHomeTypeKey(structureType);
					this.homeTypeCounts.put(structureType, nbtHomesData.hasKey(key) ? nbtHomesData.getInteger(key) : 0);
				}
			}
			
			for (int homeIndex = 0; homeIndex < this.homesCount; homeIndex++) {
				String key = getHomeKey(homeIndex);
				
				if (nbtHomesData.hasKey(key)) {
					HomeData home = new HomeData();
					home.readNBT(nbtHomesData.getCompoundTag(key));
					this.homes.add(home);
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtHomesData = new NBTTagCompound();
	
		nbtHomesData.setInteger(NBTTAG_VILLAGE_HOMESCOUNT, this.homesCount);

		if (this.homeTypeCounts != null) {
			for (Entry<VillageStructureType, Integer> structureType : this.homeTypeCounts.entrySet()) {
				nbtHomesData.setInteger(getHomeTypeKey(structureType.getKey()), structureType.getValue());
			}
		}
		
		if (this.homes != null) {
			int homeIndex = 0;
			for (HomeData structure : this.homes) {
				NBTTagCompound nbtHomeData = new NBTTagCompound();
				structure.writeNBT(nbtHomeData);
				nbtHomesData.setTag(getHomeKey(homeIndex++), nbtHomeData);
			}
		}
		
		nbtTag.setTag(NBTTAG_VILLAGE_HOMES, nbtHomesData);
	}
	
	public static String getHomeTypeKey(VillageStructureType structureType) {
		return structureType.name();
	}

	public static String getHomeKey(int homeIndex) {
		return "home@" + homeIndex;
	}
	
}
