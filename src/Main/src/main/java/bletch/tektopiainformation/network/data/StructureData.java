package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class StructureData {
	
	private static final String NBTTAG_VILLAGE_STRUCTUREID = "villagestructureid";
	private static final String NBTTAG_VILLAGE_STRUCTURETYPE = "villagestructuretype";
	private static final String NBTTAG_VILLAGE_STRUCTUREPOSITION = "villagestructureposition";
	private static final String NBTTAG_VILLAGE_STRUCTUREVALID = "villagestructurevalid";
	private static final String NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT = "villagestructurefloortilecount";
	private static final String NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER = "villagestructuretilespervillager";
	private static final String NBTTAG_VILLAGE_STRUCTUREOCCUPANTCOUNT = "villagestructureoccupantcount";
	private static final String NBTTAG_VILLAGE_STRUCTUREOCCUPANTS = "villagestructureoccupants";

	private UUID structureId;
	private VillageStructureType structureType;
	private BlockPos framePosition;
	private boolean isValid;
	private int floorTileCount;
	private int tilesPerVillager;
	private int occupantCount;
	
	private List<ResidentData> occupants;
	
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
	
	public int getOccupantCount() {
		return this.occupantCount;
	}
	
	public int getTilesPerVillager() {
		return this.tilesPerVillager;
	}
	
	public List<ResidentData> getOccupants() {
		return Collections.unmodifiableList(this.occupants != null ? this.occupants : new ArrayList<ResidentData>());
	}
	
	public int getDensityRatio() {
		if (this.tilesPerVillager == 0 || this.occupantCount == 0)
			return 0;
		
		return this.floorTileCount / this.occupantCount;
	}
	
	public Boolean isOvercrowdedCurrent() {
		if (this.tilesPerVillager == 0 || this.occupantCount == 0)
			return false;
		
		int densityRatio = this.floorTileCount / this.occupantCount;
		return (densityRatio < this.tilesPerVillager);
	}
	
	private void clearData() {
		this.structureId = UUID.randomUUID();
		this.structureType = null;
		this.framePosition = null;
		this.isValid = false;
		this.floorTileCount = 0;
		this.tilesPerVillager = 0;
		this.occupantCount = 0;
		
		this.occupants = new ArrayList<ResidentData>();
	}
	
	public void populateData(VillageStructure structure) {
		clearData();
		
		if (structure != null) {
			this.structureId = structure.getItemFrame().getPersistentID();
			this.structureType = structure.type;
			this.framePosition = structure.getFramePos();
			this.isValid = structure.isValid();
			
			List<BlockPos> floorTiles = TektopiaUtils.getStructureFloorTiles(structure);
			this.floorTileCount = floorTiles == null ? 0 : floorTiles.size();

			this.tilesPerVillager = structure.type.tilesPerVillager;
			
			List<EntityVillagerTek> occupants = structure.getEntitiesInside(EntityVillagerTek.class);
			this.occupantCount = occupants.size();
			
			for (EntityVillagerTek occupant : occupants) {
				if (occupant.isRole(VillagerRole.VISITOR))
					this.occupants.add(new VisitorData(occupant));
				else
					this.occupants.add(new ResidentData(occupant));
			}
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
		this.tilesPerVillager = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER) : 0;
		this.occupantCount = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCOUNT) : 0;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS)) {
			NBTTagCompound nbtOccupantsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS);

			for (int occupantIndex = 0; occupantIndex < this.occupantCount; occupantIndex++) {
				String key = getOccupantKey(occupantIndex);
				
				if (nbtOccupantsData.hasKey(key)) {
					ResidentData occupant = new ResidentData();
					occupant.readNBT(nbtOccupantsData.getCompoundTag(key));
					this.occupants.add(occupant);
				}
			}
		}
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
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER, this.tilesPerVillager);
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCOUNT, this.occupantCount);
		
		if (this.occupants != null) {
			NBTTagCompound nbtOccupantsData = new NBTTagCompound();
			
			int occupantIndex = 0;
			for (ResidentData occupant : this.occupants) {
				NBTTagCompound nbtOccupantData = new NBTTagCompound();
				occupant.writeNBT(nbtOccupantData);
				nbtOccupantsData.setTag(getOccupantKey(occupantIndex++), nbtOccupantData);
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS, nbtOccupantsData);
		}
	}
	
	public static String getOccupantKey(int villagerIndex) {
		return "villager@" + villagerIndex;
	}
	
}
