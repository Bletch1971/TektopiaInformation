package bletch.tektopiainformation.network.data;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import bletch.common.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class StructuresData {
	
	protected static final String NBTTAG_VILLAGE_STRUCTURES = "structures";	
	protected static final String NBTTAG_VILLAGE_STRUCTURESLIST = "list";
	protected static final String NBTTAG_VILLAGE_STRUCTURETYPECOUNTS = "types";
	protected static final String NBTTAG_VILLAGE_STRUCTURETYPENAME = "name";
	protected static final String NBTTAG_VILLAGE_STRUCTURETYPECOUNT = "count";

	protected VillageData villageData;
	protected List<StructureData> structures;
	protected Map<VillageStructureType, Integer> structureTypeCounts;

	public StructuresData() {
		populateData(null, null);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getStructuresCount() {
		return this.structures == null
				? 0
				: this.structures.size();
	}
	
	public List<StructureData> getStructures() {
		return this.structures == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.structures.stream()
						.sorted(Comparator.comparing((StructureData c) -> c.getStructureType().name()).thenComparing(StructureData::getFramePosition))
						.collect(Collectors.toList()));
	}	
	
	public List<StructureData> getStructuresByType(VillageStructureType structureType) {
		return this.structures == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.structures.stream()
						.filter(s -> structureType != null && structureType.equals(s.getStructureType()))
						.sorted(Comparator.comparing(StructureData::getFramePosition))
						.collect(Collectors.toList()));
	}
	
	public List<StructureData> getStructuresOvercrowded() {
		return this.structures == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.structures.stream()
						.filter(s -> s.isOvercrowded())
						.sorted(Comparator.comparing(StructureData::getStructureTypeName))
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
				? Collections.unmodifiableMap(new LinkedHashMap<>())
				: Collections.unmodifiableMap(this.structureTypeCounts.entrySet().stream()
						.sorted(Comparator.comparing(c -> c.getKey().name()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
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
	
	protected void clearData() {		
		this.structures = new ArrayList<>();
		this.structureTypeCounts = new LinkedHashMap<>();
	}
	
	public void populateData(VillageData villageData, Village village) {
		clearData();
		
		this.villageData = villageData;
		
		if (village != null) {
			
			Map<VillageStructureType, List<VillageStructure>> structuresList = TektopiaUtils.getVillageStructures(village); 
			
			for (Entry<VillageStructureType, List<VillageStructure>> entry : structuresList.entrySet()) {
				List<VillageStructure> structures = structuresList.get(entry.getKey());
				this.structureTypeCounts.put(entry.getKey(), structures.size());

				for (VillageStructure structure : structures) {
					this.structures.add(new StructureData(villageData, structure));
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
					this.structures.add(new StructureData(villageData, nbtTagListStructures.getCompoundTagAt(index)));
				}
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtStructuresData = new NBTTagCompound();

		if (this.structureTypeCounts != null && this.structureTypeCounts.size() > 0) {
			NBTTagList nbtTagListStructureCounts = new NBTTagList();
			
			for (Entry<VillageStructureType, Integer> structureType : this.structureTypeCounts.entrySet()) {
				NBTTagCompound nbtTagStructureCount = new NBTTagCompound();
				nbtTagStructureCount.setString(NBTTAG_VILLAGE_STRUCTURETYPENAME, structureType.getKey().name());
				nbtTagStructureCount.setInteger(NBTTAG_VILLAGE_STRUCTURETYPECOUNT, structureType.getValue());
				
				nbtTagListStructureCounts.appendTag(nbtTagStructureCount);
			}
			
			if (!nbtTagListStructureCounts.hasNoTags()) {
				nbtStructuresData.setTag(NBTTAG_VILLAGE_STRUCTURETYPECOUNTS, nbtTagListStructureCounts);
			}
		}
		
		if (this.structures != null && this.structures.size() > 0) {
			NBTTagList nbtTagListHomes = new NBTTagList();

			for (StructureData structure : this.structures) {
				NBTTagCompound nbtTagStructure = structure.writeNBT(new NBTTagCompound());
				if (!nbtTagStructure.hasNoTags()) {
					nbtTagListHomes.appendTag(nbtTagStructure);
				}
			}
			
			if (!nbtTagListHomes.hasNoTags()) {
				nbtStructuresData.setTag(NBTTAG_VILLAGE_STRUCTURESLIST, nbtTagListHomes);
			}
		}
		
		if (!nbtStructuresData.hasNoTags()) {
			nbtTag.setTag(NBTTAG_VILLAGE_STRUCTURES, nbtStructuresData);
		}
		
		return nbtTag;
	}
	
}
