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
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class StructuresData {
	
	private static final String NBTTAG_VILLAGE_STRUCTURES = "villagestructures";	
	private static final String NBTTAG_VILLAGE_STRUCTURESCOUNT = "villagestructurescount";
	
	private int structuresCount;
	private List<StructureData> structures;
	private Map<VillageStructureType, Integer> structureTypeCounts;

	public StructuresData() {
		populateData(null);
	}
	
	public StructuresData(Village village) {
		populateData(village);
	}
	
	public int getStructuresCount() {
		return this.structuresCount;
	}
	
	public List<StructureData> getStructures() {
		return Collections.unmodifiableList(this.structures == null ? new ArrayList<StructureData>() : this.structures.stream()
				.sorted((c1 , c2) -> {
					int compare = c1.getStructureType().name().compareTo(c2.getStructureType().name());
					return compare != 0 ? compare : c1.getFramePosition().compareTo(c2.getFramePosition());
				})
				.collect(Collectors.toList()));
	}	
	
	public List<StructureData> getStructuresByType(VillageStructureType structureType) {
		return Collections.unmodifiableList(this.structures == null ? new ArrayList<StructureData>() : this.structures.stream()
				.filter(s -> structureType != null && structureType.equals(s.getStructureType()))
				.sorted((c1 , c2) -> c1.getFramePosition().compareTo(c2.getFramePosition()))
				.collect(Collectors.toList()));
	}
	
	public List<StructureData> getStructuresOvercrowded() {
		return Collections.unmodifiableList(this.structures == null ? new ArrayList<StructureData>() : this.structures.stream()
				.filter(s -> s.isOvercrowdedCurrent())
				.sorted((c1 , c2) -> c1.getStructureTypeName().compareTo(c2.getStructureTypeName()))
				.collect(Collectors.toList()));
	}
	
	public StructureData getStructure(int index) {
		return this.structures == null ? null : this.structures.get(index);
	}
	
	public StructureData getStructureById(int structureId) {
		return this.structures == null ? null : this.structures.stream()
				.filter(r -> structureId > 0 && structureId == r.getStructureId())
				.findFirst().orElse(null);
	}
	
	public StructureData getStructureByFramePosition(BlockPos framePosition) {
		return this.structures == null ? null : this.structures.stream()
				.filter(h -> framePosition != null && framePosition.equals(h.getFramePosition()))
				.findFirst().orElse(null);
	}
	
	public Map<VillageStructureType, Integer> getStructureTypeCounts() {
		return Collections.unmodifiableMap(this.structureTypeCounts == null ? new HashMap<VillageStructureType, Integer>() : this.structureTypeCounts);
	}	
	
	public int getStructureTypeCount(VillageStructureType structureType) {
		return structureType != null && this.structureTypeCounts != null && this.structureTypeCounts.containsKey(structureType) ? this.structureTypeCounts.get(structureType) : 0;
	}
	
	public StructureData getTownHall() {
		return this.structures == null ? null : this.structures.stream()
				.filter(h -> h.getStructureType() == VillageStructureType.TOWNHALL)
				.findFirst().orElse(null);
	}
	
	private void clearData() {
		this.structuresCount = 0;
		
		this.structures = new ArrayList<StructureData>();
		this.structureTypeCounts = new HashMap<VillageStructureType, Integer>();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			
			Map<VillageStructureType, List<VillageStructure>> structuresList = TektopiaUtils.getVillageStructures(village); 

			final int[] totalStructureCount = { 0 };
			structuresList.entrySet().stream()
				.forEach(s -> totalStructureCount[0] += s.getValue().size());
		
			this.structuresCount = totalStructureCount[0];
			
			for (VillageStructureType structureType : TektopiaUtils.getVillageStructureTypes()) {
				this.structureTypeCounts.put(structureType, 0);
				
				if (structuresList.containsKey(structureType)) {
					List<VillageStructure> structures = structuresList.get(structureType);
					this.structureTypeCounts.put(structureType, structures.size());

					for (VillageStructure structure : structures) {
						this.structures.add(new StructureData(structure));
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

		if (nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURES)) {
			NBTTagCompound nbtStructuresData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_STRUCTURES);
			
			this.structuresCount = nbtStructuresData.hasKey(NBTTAG_VILLAGE_STRUCTURESCOUNT) ? nbtStructuresData.getInteger(NBTTAG_VILLAGE_STRUCTURESCOUNT) : 0;
			
			for (VillageStructureType structureType : TektopiaUtils.getVillageStructureTypes()) {
				String key = getStructureTypeKey(structureType);
				this.structureTypeCounts.put(structureType, nbtStructuresData.hasKey(key) ? nbtStructuresData.getInteger(key) : 0);
			}
			
			for (int structureIndex = 0; structureIndex < this.structuresCount; structureIndex++) {
				String key = getStructureKey(structureIndex);
				
				if (nbtStructuresData.hasKey(key)) {
					StructureData structure = new StructureData();
					structure.readNBT(nbtStructuresData.getCompoundTag(key));
					this.structures.add(structure);
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtStructuresData = new NBTTagCompound();

		nbtStructuresData.setInteger(NBTTAG_VILLAGE_STRUCTURESCOUNT, this.structuresCount);

		if (this.structureTypeCounts != null) {
			for (Entry<VillageStructureType, Integer> structureType : this.structureTypeCounts.entrySet()) {
				nbtStructuresData.setInteger(getStructureTypeKey(structureType.getKey()), structureType.getValue());
			}
		}
		
		if (this.structures != null) {
			int structureIndex = 0;
			for (StructureData structure : this.structures) {
				NBTTagCompound nbtStructureData = new NBTTagCompound();
				structure.writeNBT(nbtStructureData);
				nbtStructuresData.setTag(getStructureKey(structureIndex++), nbtStructureData);
			}
		}
		
		nbtTag.setTag(NBTTAG_VILLAGE_STRUCTURES, nbtStructuresData);
	}
	
	public static String getStructureTypeKey(VillageStructureType structureType) {
		return structureType.name();
	}
	
	public static String getStructureKey(int structureIndex) {
		return "structure@" + structureIndex;
	}
	
}
