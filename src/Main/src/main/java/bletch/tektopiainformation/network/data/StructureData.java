package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
	private static final String NBTTAG_VILLAGE_STRUCTUREOCCUPANTS = "villagestructureoccupants";
	private static final String NBTTAG_VILLAGE_STRUCTUREOCCUPANTCLASS = "villagestructureoccupantclass";
	
	private static Random rand = new Random();

	private int structureId;
	private VillageStructureType structureType;
	private BlockPos framePosition;
	private boolean isValid;
	private int floorTileCount;
	private int tilesPerVillager;
	
	private List<ResidentData> occupants;
	
	public StructureData() {
		populateData(null);
	}
	
	public StructureData(VillageStructure structure) {
		populateData(structure);
	}
	
	public StructureData(NBTTagCompound nbtTag) {
		readNBT(nbtTag);
	}
	
	public int getStructureId() {
		return this.structureId;
	}
	
	public VillageStructureType getStructureType() {
		return this.structureType;
	}
	
	public String getStructureTypeName() {
		return this.structureType != null && this.structureType.itemStack != null 
				? this.structureType.itemStack.getDisplayName() 
				: "";
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
	
	public int getTilesPerVillager() {
		return this.tilesPerVillager;
	}
	
	public int getOccupantCount() {
		return this.occupants == null
				? 0
				: this.occupants.size();
	}
	
	public List<ResidentData> getOccupants() {
		return this.occupants == null
				? Collections.unmodifiableList(new ArrayList<ResidentData>())
				: Collections.unmodifiableList(this.occupants);
	}
	
	public int getDensityRatio() {
		if (this.tilesPerVillager == 0 || this.getOccupantCount() == 0)
			return 0;
		
		return this.floorTileCount / this.getOccupantCount();
	}
	
	public Boolean isOvercrowdedCurrent() {
		if (this.tilesPerVillager == 0 || this.getOccupantCount() == 0)
			return false;
		
		int densityRatio = this.floorTileCount / this.getOccupantCount();
		return (densityRatio < this.tilesPerVillager);
	}
	
	private void clearData() {
		this.structureId = rand.nextInt();
		this.structureType = null;
		this.framePosition = null;
		this.isValid = false;
		this.floorTileCount = 0;
		this.tilesPerVillager = 0;
		
		this.occupants = new ArrayList<ResidentData>();
	}
	
	public void populateData(VillageStructure structure) {
		clearData();
		
		if (structure != null) {
			this.structureId = structure.getItemFrame().getEntityId();
			this.structureType = structure.type;
			this.framePosition = structure.getFramePos();
			this.isValid = structure.isValid();
			
			List<BlockPos> floorTiles = TektopiaUtils.getStructureFloorTiles(structure);
			this.floorTileCount = floorTiles == null ? 0 : floorTiles.size();

			this.tilesPerVillager = structure.type.tilesPerVillager;
			
			List<EntityVillagerTek> occupants = structure.getEntitiesInside(EntityVillagerTek.class);
			
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
		
		this.structureId = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREID) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREID) : rand.nextInt();
		this.structureType = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURETYPE) ? VillageStructureType.valueOf(nbtTag.getString(NBTTAG_VILLAGE_STRUCTURETYPE)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_STRUCTUREPOSITION)) : null;
		this.isValid = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREVALID) ? nbtTag.getBoolean(NBTTAG_VILLAGE_STRUCTUREPOSITION) : false;
		this.floorTileCount = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT) : 0;
		this.tilesPerVillager = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER) : 0;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS)) {
			NBTTagList nbtTagListOccupants = nbtTag.getTagList(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS, 10);
			
			for (int index = 0; index < nbtTagListOccupants.tagCount(); index++) {
				NBTTagCompound nbtTagOccupant = nbtTagListOccupants.getCompoundTagAt(index);
				
				if (nbtTagOccupant.hasKey(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCLASS)) {
					String className = nbtTagOccupant.getString(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCLASS);
					
					if (className.equals(VisitorData.class.getSimpleName())) {
						this.occupants.add(new VisitorData(nbtTagOccupant));
					} else {
						this.occupants.add(new ResidentData(nbtTagOccupant));
					}
				}
				
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}

		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTUREID, this.structureId);
		if (this.structureType != null) {
			nbtTag.setString(NBTTAG_VILLAGE_STRUCTURETYPE, this.structureType.name());
		}
		if (this.framePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_STRUCTUREPOSITION, this.framePosition.toLong());
		}
		nbtTag.setBoolean(NBTTAG_VILLAGE_STRUCTUREVALID, this.isValid);
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT, this.floorTileCount);
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTURETILESPERVILLAGER, this.tilesPerVillager);
		
		if (this.occupants != null) {
			NBTTagList nbtTagListOccupants = new NBTTagList();
			
			for (ResidentData occupant : this.occupants) {
				NBTTagCompound nbtOccupant = new NBTTagCompound();
				
				if (occupant instanceof VisitorData) {
					((VisitorData)occupant).writeNBT(nbtOccupant);
					nbtOccupant.setString(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCLASS, VisitorData.class.getSimpleName());
				} else {
					occupant.writeNBT(nbtOccupant);
					nbtOccupant.setString(NBTTAG_VILLAGE_STRUCTUREOCCUPANTCLASS, ResidentData.class.getSimpleName());
				}
				
				nbtTagListOccupants.appendTag(nbtOccupant);
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS, nbtTagListOccupants);
		}
	}
	
}
