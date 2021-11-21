package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import bletch.common.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class StructureData {
	
	protected static final String NBTTAG_VILLAGE_STRUCTUREID = "villagestructureid";
	protected static final String NBTTAG_VILLAGE_STRUCTURETYPE = "villagestructuretype";
	protected static final String NBTTAG_VILLAGE_STRUCTUREPOSITION = "villagestructureposition";
	protected static final String NBTTAG_VILLAGE_STRUCTUREVALID = "villagestructurevalid";
	protected static final String NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT = "villagestructurefloortilecount";
	protected static final String NBTTAG_VILLAGE_STRUCTURETILESPEROCCUPANT = "villagestructuretilesperoccupant";
	protected static final String NBTTAG_VILLAGE_STRUCTUREOCCUPANTS = "villagestructureoccupants";
	protected static final String NBTTAG_VILLAGE_STRUCTUREANIMALPEN = "villagestructureanimalpen";
	protected static final String NBTTAG_VILLAGE_STRUCTUREANIMALCOUNT = "villagestructureanimalcount";
	protected static final String NBTTAG_VILLAGE_STRUCTUREANIMALSIZE = "villagestructureanimalsize";
	
	protected static final Random rand = new Random();
	protected VillageData villageData;

	protected int structureId;
	protected VillageStructureType structureType;
	protected BlockPos framePosition;
	protected boolean isValid;
	protected int floorTileCount;
	protected int tilesPerOccupant;

	protected boolean isAnimalPen;
	protected int animalCount;
	protected int animalSize;
	
	protected List<ResidentData> occupants;
	
	public StructureData(VillageData villageData, VillageStructure structure) {
		this.villageData = villageData;
		
		populateData(structure);
	}
	
	public StructureData(VillageData villageData, NBTTagCompound nbtTag) {
		this.villageData = villageData;
		
		readNBT(nbtTag);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
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
	
	public int getTilesPerOccupant() {
		return this.tilesPerOccupant;
	}
	
	public boolean isAnimalPen() {
		return this.isAnimalPen;
	}
	
	public int getAnimalCount() {
		return this.animalCount;
	}
	
	public int getAnimalSize() {
		return this.animalSize;
	}
	
	public int getOccupantCount() {
		return this.occupants == null
				? 0
				: this.occupants.size();
	}
	
	public List<ResidentData> getOccupants() {
		return this.occupants == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.occupants);
	}
	
	public int getOccupantDensityRatio() {
		if (this.tilesPerOccupant == 0 || this.getOccupantCount() == 0)
			return 0;
		
		return this.floorTileCount / this.getOccupantCount();
	}
	
	public Boolean isOvercrowded() {
		int densityRatio = this.getOccupantDensityRatio();
		return (densityRatio > 0 && densityRatio < this.tilesPerOccupant);
	}
	
	public int getAnimalDensityRatio() {
		if (this.animalSize == 0 || this.animalCount == 0)
			return 0;
		
		return this.floorTileCount / this.animalCount;
	}
	
	public Boolean isPenFull() {
		int densityRatio = this.getAnimalDensityRatio();
		return (densityRatio < this.animalSize);
	}
	
	protected void clearData() {
		this.structureId = rand.nextInt();
		this.structureType = null;
		this.framePosition = null;
		this.isValid = false;
		this.floorTileCount = 0;
		this.tilesPerOccupant = 0;
		
		this.isAnimalPen = false;
		this.animalCount = 0;
		this.animalSize = 0;
		
		this.occupants = new ArrayList<>();
	}
	
	protected void populateData(VillageStructure structure) {
		clearData();
		
		if (structure != null) {
			this.structureId = structure.getItemFrame().getEntityId();
			this.structureType = structure.type;
			this.framePosition = structure.getFramePos();
			this.isValid = structure.isValid();
			
			List<BlockPos> floorTiles = TektopiaUtils.getStructureFloorTiles(structure);
			this.floorTileCount = floorTiles == null ? 0 : floorTiles.size();

			this.tilesPerOccupant = structure.type.tilesPerVillager;
			
			List<EntityVillagerTek> occupants = structure.getEntitiesInside(EntityVillagerTek.class);
			
			for (EntityVillagerTek occupant : occupants) {
				if (occupant.isRole(VillagerRole.VISITOR))
					this.occupants.add(new VisitorData(occupant));
				else
					this.occupants.add(new ResidentData(occupant));
			}
			
			if (structure instanceof VillageStructureRancherPen) {
				VillageStructureRancherPen rancherPen = (VillageStructureRancherPen)structure;
				
				this.isAnimalPen = true;
				this.animalCount = TektopiaUtils.getStructureAnimalCount(rancherPen);
				this.animalSize = rancherPen.getAnimalSize();
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
		this.isValid = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREVALID) && nbtTag.getBoolean(NBTTAG_VILLAGE_STRUCTUREPOSITION);
		this.floorTileCount = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREFLOORTILECOUNT) : 0;
		this.tilesPerOccupant = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTURETILESPEROCCUPANT) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTURETILESPEROCCUPANT) : 0;

		this.isAnimalPen = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREANIMALPEN) && nbtTag.getBoolean(NBTTAG_VILLAGE_STRUCTUREANIMALPEN);
		this.animalCount = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREANIMALCOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREANIMALCOUNT) : 0;
		this.animalSize = nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREANIMALSIZE) ? nbtTag.getInteger(NBTTAG_VILLAGE_STRUCTUREANIMALSIZE) : 0;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS)) {
			NBTTagList nbtTagListOccupants = nbtTag.getTagList(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS, 3);
			
			for (int index = 0; index < nbtTagListOccupants.tagCount(); index++) {
				int nbtTagId = nbtTagListOccupants.getIntAt(index);
				
				if (this.villageData != null) {
					ResidentData occupant = this.villageData.getResidentsData().getResidentById(nbtTagId);
					if (occupant == null) {
						occupant = this.villageData.getVisitorsData().getVisitorById(nbtTagId);
					}
					
					if (occupant != null) {
						this.occupants.add(occupant);
					}
				}
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
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
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTURETILESPEROCCUPANT, this.tilesPerOccupant);

		nbtTag.setBoolean(NBTTAG_VILLAGE_STRUCTUREANIMALPEN, this.isAnimalPen);
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTUREANIMALCOUNT, this.animalCount);
		nbtTag.setInteger(NBTTAG_VILLAGE_STRUCTUREANIMALSIZE, this.animalSize);
		
		if (this.occupants != null) {
			NBTTagList nbtTagListOccupants = new NBTTagList();
			
			for (ResidentData occupant : this.occupants) {
				nbtTagListOccupants.appendTag(new NBTTagInt(occupant.getId()));
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_STRUCTUREOCCUPANTS, nbtTagListOccupants);
		}
		
		return nbtTag;
	}
	
}
