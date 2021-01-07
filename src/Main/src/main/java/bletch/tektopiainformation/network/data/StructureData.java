package bletch.tektopiainformation.network.data;

import java.util.List;
import java.util.UUID;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class StructureData {
	
	private static final String NBTTAG_VILLAGE_STRUCTUREID = "villagestructureid";
	private static final String NBTTAG_VILLAGE_STRUCTURETYPE = "villagestructuretype";
	private static final String NBTTAG_VILLAGE_STRUCTUREPOSITION = "villagestructureposition";
	private static final String NBTTAG_VILLAGE_STRUCTUREVALID = "villagestructurevalid";
	private static final String NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT = "villagestructurefloortilecount";

	private UUID structureId;
	private VillageStructureType structureType;
	private BlockPos framePosition;
	private boolean isValid;
	private int floorTileCount;
	
	public StructureData() {
		populateData(null);
	}
	
	public StructureData(VillageStructure structure) {
		populateData(structure);
	}
	
	public UUID getStructureId() {
		return this.structureId;
	}
	
	public VillageStructureType getStructureType() {
		return this.structureType;
	}
	
	public String getStructureTypeName() {
		return this.structureType != null && this.structureType.itemStack != null ? this.structureType.itemStack.getDisplayName() : "";
	}
	
	public BlockPos getFramePosition() {
		return this.framePosition;
	}
	
	public boolean isValid() {
		return this.isValid;
	}
	
	public int getFloorTileCount() {
		return this.floorTileCount;
	}
	
	private void clearData() {
		this.structureId = UUID.randomUUID();
		this.structureType = null;
		this.framePosition = null;
		this.isValid = false;
		this.floorTileCount = 0;
	}
	
	public void populateData(VillageStructure structure) {
		clearData();
		
		if (structure != null) {
			this.structureType = structure.type;
			this.framePosition = structure.getFramePos();
			this.isValid = structure.isValid();
			
			List<BlockPos> floorTiles = TektopiaUtils.getStructureFloorTiles(structure);
			this.floorTileCount = floorTiles == null ? 0 : floorTiles.size();
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.structureId = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREID) ? nbtTag.getUniqueId(NBTTAG_VILLAGE_STRUCTUREID) : UUID.randomUUID();
		this.structureType = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURETYPE) ? VillageStructureType.valueOf(nbtTag.getString(NBTTAG_VILLAGE_STRUCTURETYPE)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_STRUCTUREPOSITION)) : null;
		this.isValid = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREVALID) ? nbtTag.getBoolean(NBTTAG_VILLAGE_STRUCTUREPOSITION) : false;
		this.floorTileCount = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT) : 0;
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}

		nbtTag.setUniqueId(NBTTAG_VILLAGE_STRUCTUREID, this.structureId);
		if (this.structureType != null) {
			nbtTag.setString(NBTTAG_VILLAGE_STRUCTURETYPE, this.structureType.name());
		}
		if (this.framePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_STRUCTUREPOSITION, this.framePosition.toLong());
		}
		nbtTag.setBoolean(NBTTAG_VILLAGE_STRUCTUREVALID, this.isValid);
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT, this.floorTileCount);
	}
	
}
