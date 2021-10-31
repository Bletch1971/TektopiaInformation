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

public class StructuresData {
	
	private static final String NBTTAG_VILLAGE_STRUCTURES = "villagestructures";	
	private static final String NBTTAG_VILLAGE_STRUCTURESLIST = "villagestructureslist";
	private static final String NBTTAG_VILLAGE_STRUCTURETYPECOUNTS = "villagestructuretypecounts";
	private static final String NBTTAG_VILLAGE_STRUCTURETYPENAME = "villagestructuretypename";
	private static final String NBTTAG_VILLAGE_STRUCTURETYPECOUNT = "villagestructuretypecount";
	
	private List<StructureData> structures;
	private Map<VillageStructureType, Integer> structureTypeCounts;

	public StructuresData() {
		populateData(null);
	}
	
	public StructuresData(Village village) {
		populateData(village);
	}
	
	public int getStructuresCount() {
		return this.structures == null
				? 0
				: this.structures.size();
	}
	
	public List<StructureData> getStructures() {
		return this.structures == null
				? Collections.unmodifiableList(new ArrayList<StructureData>())
				: Collections.unmodifiableList(this.structures.stream()
						.sorted((c1 , c2) -> {
							int compare = c1.getStructureType().name().compareTo(c2.getStructureType().name());
							return compare != 0 ? compare : c1.getFramePosition().compareTo(c2.getFramePosition());
						})
						.collect(Collectors.toList()));
	}	
	
	public List<StructureData> getStructuresByType(VillageStructureType structureType) {
		return this.structures == null
				? Collections.unmodifiableList(new ArrayList<StructureData>())
				: Collections.unmodifiableList(this.structures.stream()
						.filter(s -> structureType != null && structureType.equals(s.getStructureType()))
						.sorted((c1 , c2) -> c1.getFramePosition().compareTo(c2.getFramePosition()))
						.collect(Collectors.toList()));
	}
	
	public List<StructureData> getStructuresOvercrowded() {
		return this.structures == null
				? Collections.unmodifiableList(new ArrayList<StructureData>())
				: Collections.unmodifiableList(this.structures.stream()
						.filter(s -> s.isOvercrowdedCurrent())
						.sorted((c1 , c2) -> c1.getStructureTypeName().compareTo(c2.getStructureTypeName()))
						.collect(Collectors.toList()));
	}
	
	public StructureData getStructure(int index) {
		return this.structures == null 
				? null 
				: this.structures.get(index);
	}
	
	public StructureData getStructureById(int structureId) {
		return this.structures == null 
				? null 
				: this.structures.stream()
						.filter(r -> structureId > 0 && structureId == r.getStructureId())
						.findFirst().orElse(null);
	}
	
	public StructureData getStructureByFramePosition(BlockPos framePosition) {
		return this.structures == null 
				? null 
				: this.structures.stream()
						.filter(h -> framePosition != null && framePosition.equals(h.getFramePosition()))
						.findFirst().orElse(null);
	}
	
	public Map<VillageStructureType, Integer> getStructureTypeCounts() {
		return this.structureTypeCounts == null
				? Collections.unmodifiableMap(new HashMap<VillageStructureType, Integer>())
				: Collections.unmodifiableMap(this.structureTypeCounts);
	}	
	
	public int getStructureTypeCount(VillageStructureType structureType) {
		return structureType != null && this.structureTypeCounts != null && this.structureTypeCounts.containsKey(structureType) 
				? this.structureTypeCounts.get(structureType) 
				: 0;
	}
	
	public StructureData getTownHall() {
		return this.structures == null 
				? null 
				: this.structures.stream()
						.filter(h -> h.getStructureType() == VillageStructureType.TOWNHALL)
						.findFirst().orElse(null);
	}
	
	private void clearData() {		
		this.structures = new ArrayList<StructureData>();
		this.structureTypeCounts = new HashMap<VillageStructureType, Integer>();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			
			Map<VillageStructureType, List<VillageStructure>> structuresList = TektopiaUtils.getVillageStructures(village); 
			
			for (Entry<VillageStructureType, List<VillageStructure>> entry : structuresList.entrySet()) {
				List<VillageStructure> structures = structuresList.get(entry.getKey());
				this.structureTypeCounts.put(entry.getKey(), structures.size());

				for (VillageStructure structure : structures) {
					this.structures.add(new StructureData(structure));
				}
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();

		if (nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURES)) {
			NBTTagCompound nbtStructuresData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_STRUCTURES);
			
			if (nbtStructuresData.hasKey(NBTTAG_VILLAGE_STRUCTURETYPECOUNTS)) {
				NBTTagList nbtTagListStructureCounts = nbtStructuresData.getTagList(NBTTAG_VILLAGE_STRUCTURETYPECOUNTS, 10);
				
				for (int index = 0; index < nbtTagListStructureCounts.tagCount(); index++) {
					NBTTagCompound nbtTagStructureCount = nbtTagListStructureCounts.getCompoundTagAt(index);
					VillageStructureType structureType = VillageStructureType.valueOf(nbtTagStructureCount.getString(NBTTAG_VILLAGE_STRUCTURETYPENAME));
					
					this.structureTypeCounts.put(structureType, nbtTagStructureCount.getInteger(NBTTAG_VILLAGE_STRUCTURETYPECOUNT));
				}	
			}
			
			if (nbtStructuresData.hasKey(NBTTAG_VILLAGE_STRUCTURESLIST)) {
				NBTTagList nbtTagListStructures = nbtStructuresData.getTagList(NBTTAG_VILLAGE_STRUCTURESLIST, 10);
				
				for (int index = 0; index < nbtTagListStructures.tagCount(); index++) {
					NBTTagCompound nbtTagStructure = nbtTagListStructures.getCompoundTagAt(index);
					
					this.structures.add(new StructureData(nbtTagStructure));
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtStructuresData = new NBTTagCompound();

		if (this.structureTypeCounts != null) {
			NBTTagList nbtTagListStructureCounts = new NBTTagList();
			
			for (Entry<VillageStructureType, Integer> structureType : this.structureTypeCounts.entrySet()) {
				NBTTagCompound nbtTagStructureCount = new NBTTagCompound();
				nbtTagStructureCount.setString(NBTTAG_VILLAGE_STRUCTURETYPENAME, structureType.getKey().name());
				nbtTagStructureCount.setInteger(NBTTAG_VILLAGE_STRUCTURETYPECOUNT, structureType.getValue());
				
				nbtTagListStructureCounts.appendTag(nbtTagStructureCount);
			}
			
			nbtStructuresData.setTag(NBTTAG_VILLAGE_STRUCTURETYPECOUNTS, nbtTagListStructureCounts);
		}
		
		if (this.structures != null) {
			NBTTagList nbtTagListHomes = new NBTTagList();

			for (StructureData structure : this.structures) {
				NBTTagCompound nbtStructure = new NBTTagCompound();
				structure.writeNBT(nbtStructure);
				
				nbtTagListHomes.appendTag(nbtStructure);
			}
			
			nbtStructuresData.setTag(NBTTAG_VILLAGE_STRUCTURESLIST, nbtTagListHomes);
		}
		
		nbtTag.setTag(NBTTAG_VILLAGE_STRUCTURES, nbtStructuresData);
	}
	
}
