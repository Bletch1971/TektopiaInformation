package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureBarracks;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureType;

public class HomeData {

	protected static final String NBTTAG_VILLAGE_HOMEID = "villagehomeid";
	protected static final String NBTTAG_VILLAGE_HOMETYPE = "villagehometype";
	protected static final String NBTTAG_VILLAGE_HOMEPOSITION = "villagehomeposition";
	protected static final String NBTTAG_VILLAGE_HOMEVALID = "villagehomevalid";
	protected static final String NBTTAG_VILLAGE_HOMEFLOORTILECOUNT = "villagehomefloortilecount";
	protected static final String NBTTAG_VILLAGE_HOMETILESPERVILLAGER = "villagehometilespervillager";
	protected static final String NBTTAG_VILLAGE_HOMEMAXBEDS = "villagehomemaxbeds";
	protected static final String NBTTAG_VILLAGE_HOMERESIDENTS = "villagehomeresidents";
	protected static final String NBTTAG_VILLAGE_HOMEBEDPOSITIONS = "villagehomebedpositions";
	protected static final String NBTTAG_VILLAGE_HOMEBEDPOSITION = "villagehomebedposition";
	
	protected static final Random rand = new Random();
	protected VillageData villageData;

	protected int homeId;
	protected VillageStructureType structureType;
	protected BlockPos framePosition;
	protected boolean isValid;
	protected int floorTileCount;
	protected int maxBeds;
	protected int tilesPerVillager;

	protected List<ResidentData> residents;
	protected List<BlockPos> bedPositions;
	
	public HomeData(VillageData villageData, VillageStructure structure) {
		this.villageData = villageData;
		
		populateData(structure);
	}
	
	public HomeData(VillageData villageData, NBTTagCompound nbtTag) {
		this.villageData = villageData;
		
		readNBT(nbtTag);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getHomeId() {
		return this.homeId;
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
	
	public int getMaxBeds() {
		return this.maxBeds;
	}
	
	public int getTilesPerVillager() {
		return this.tilesPerVillager;
	}
	
	public int getResidentsCount() {
		return this.residents == null 
				? 0 
				: this.residents.size();
	}
	
	public boolean isFull() {
		return this.residents.size() >= this.maxBeds;
	}
	
	public List<ResidentData> getResidents() {
		return this.residents == null
				? Collections.unmodifiableList(new ArrayList<ResidentData>())
				: Collections.unmodifiableList(this.residents);
	}
	
	public ResidentData getResidentByBedPosition(BlockPos bedPosition) {
		return this.residents == null 
				? null 
				: this.residents.stream()
						.filter(r -> bedPosition != null && bedPosition.equals(r.getBedPosition()))
						.findFirst().orElse(null);
	}
	
	public int getBedCount() {
		return this.bedPositions == null 
				? 0 
				: this.bedPositions.size();
	}
	
	public List<BlockPos> getBedPositions() {
		return this.bedPositions == null 
				? new ArrayList<BlockPos>() 
				: Collections.unmodifiableList(this.bedPositions.stream()
						.sorted((c1 , c2) -> c1.compareTo(c2))
						.collect(Collectors.toList()));
	}
	
	public boolean hasBedPosition(BlockPos bedPosition) {
		return this.bedPositions != null && bedPosition != null && this.bedPositions.stream()
				.anyMatch(p -> bedPosition.equals(p));
	}
	
	public int getAdultCount() {
		return this.residents == null 
				? 0 
				: (int)this.residents.stream()
						.filter(r -> !r.isChild())
						.count();
	}
	
	public int getChildCount() {
		return this.residents == null 
				? 0 
				: (int)this.residents.stream()
						.filter(r -> r.isChild())
						.count();
	}
	
	public int getMaleCount() {
		return this.residents == null 
				? 0 
				: (int)this.residents.stream()
						.filter(r -> r.isMale())
						.count();
	}
	
	public int getFemaleCount() {
		return this.residents == null 
				? 0 
				: (int)this.residents.stream()
						.filter(r -> !r.isMale())
						.count();
	}
	
	public int getDensityRatio() {
		if (this.tilesPerVillager == 0 || this.maxBeds == 0)
			return 0;
		
		return this.floorTileCount / this.maxBeds;
	}
	
	public Boolean isOvercrowded() {
		if (this.tilesPerVillager == 0 || this.maxBeds == 0)
			return false;
		
		int densityRatio = this.floorTileCount / this.maxBeds;
		return (densityRatio < this.tilesPerVillager);
	}
	
	protected void clearData() {
		this.homeId = rand.nextInt();
		this.structureType = null;
		this.framePosition = null;
		this.isValid = false;
		this.floorTileCount = 0;
		this.maxBeds = 0;
		this.tilesPerVillager = 0;
		
		this.residents = new ArrayList<ResidentData>();
		this.bedPositions = new ArrayList<BlockPos>();
	}
	
	protected void populateData(VillageStructure structure) {
		clearData();
		
		List<VillageStructureType> homeTypes = TektopiaUtils.getVillageHomeTypes();

		if (structure != null && homeTypes != null && homeTypes.contains(structure.type)) {
			this.homeId = structure.getItemFrame().getEntityId();
			this.structureType = structure.type;
			this.framePosition = structure.getFramePos();
			this.isValid = structure.isValid();
			
			List<BlockPos> floorTiles = TektopiaUtils.getStructureFloorTiles(structure);
			this.floorTileCount = floorTiles == null ? 0 : floorTiles.size();

			this.tilesPerVillager = structure.type.tilesPerVillager;
			
			if (structure instanceof VillageStructureBarracks) {
				VillageStructureBarracks barracks = (VillageStructureBarracks)structure;
				
				this.maxBeds = TektopiaUtils.getStructureMaxBeds(barracks);
				
				for (EntityVillagerTek resident : barracks.getResidents()) {
					this.residents.add(new ResidentData(resident));
				}

				this.bedPositions = new ArrayList<BlockPos>(barracks.getSpecialBlocks(Blocks.BED));
			}
			else if (structure instanceof VillageStructureHome) {
				VillageStructureHome home = (VillageStructureHome)structure;
				
				this.maxBeds = TektopiaUtils.getStructureMaxBeds(home);
				
				for (EntityVillagerTek resident : home.getResidents()) {
					this.residents.add(new ResidentData(resident));
				}
				
				this.bedPositions = new ArrayList<BlockPos>(home.getSpecialBlocks(Blocks.BED));
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.homeId = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEID) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMEID) : rand.nextInt();
		this.structureType = nbtTag.hasKey(NBTTAG_VILLAGE_HOMETYPE) ? VillageStructureType.valueOf(nbtTag.getString(NBTTAG_VILLAGE_HOMETYPE)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_HOMEPOSITION)) : null;
		this.isValid = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEVALID) ? nbtTag.getBoolean(NBTTAG_VILLAGE_HOMEVALID) : false;
		this.floorTileCount = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEFLOORTILECOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMEFLOORTILECOUNT) : 0;
		this.tilesPerVillager = nbtTag.hasKey(NBTTAG_VILLAGE_HOMETILESPERVILLAGER) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMETILESPERVILLAGER) : 0;
		this.maxBeds = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEMAXBEDS) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMEMAXBEDS) : 0;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_HOMERESIDENTS)) {
			NBTTagList nbtTagListResidents = nbtTag.getTagList(NBTTAG_VILLAGE_HOMERESIDENTS, 3);
			
			for (int index = 0; index < nbtTagListResidents.tagCount(); index++) {
				int nbtTagId = nbtTagListResidents.getIntAt(index);
				
				if (this.villageData != null) {
					ResidentData resident = this.villageData.getResidentsData().getResidentById(nbtTagId);
					if (resident == null) {
						resident = this.villageData.getVisitorsData().getVisitorById(nbtTagId);
					}
					
					if (resident != null) {
						this.residents.add(resident);
					}
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_HOMEBEDPOSITIONS)) {
			NBTTagList nbtTagListBedPositions = nbtTag.getTagList(NBTTAG_VILLAGE_HOMEBEDPOSITIONS, 10);
			
			for (int index = 0; index < nbtTagListBedPositions.tagCount(); index++) {
				NBTTagCompound nbtTagBedPosition = nbtTagListBedPositions.getCompoundTagAt(index);
				
				this.bedPositions.add(BlockPos.fromLong(nbtTagBedPosition.getLong(NBTTAG_VILLAGE_HOMEBEDPOSITION)));
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}

		nbtTag.setInteger(NBTTAG_VILLAGE_HOMEID, this.homeId);
		if (this.structureType != null) {
			nbtTag.setString(NBTTAG_VILLAGE_HOMETYPE, this.structureType.name());
		}
		if (this.framePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_HOMEPOSITION, this.framePosition.toLong());
		}
		nbtTag.setBoolean(NBTTAG_VILLAGE_HOMEVALID, this.isValid);
		nbtTag.setInteger(NBTTAG_VILLAGE_HOMEFLOORTILECOUNT, this.floorTileCount);
		nbtTag.setInteger(NBTTAG_VILLAGE_HOMETILESPERVILLAGER, this.tilesPerVillager);
		nbtTag.setInteger(NBTTAG_VILLAGE_HOMEMAXBEDS, this.maxBeds);
		
		if (this.residents != null) {
			NBTTagList nbtTagListResidents = new NBTTagList();
			
			for (ResidentData resident : this.residents) {
				nbtTagListResidents.appendTag(new NBTTagInt(resident.getId()));
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_HOMERESIDENTS, nbtTagListResidents);
		}
		
		if (this.bedPositions != null) {
			NBTTagList nbtTagListBedPositions = new NBTTagList();
			
			for (BlockPos bedPosition : this.bedPositions) {
				if (bedPosition != null) {
					NBTTagCompound nbtTagBedPosition = new NBTTagCompound();
					nbtTagBedPosition.setLong(NBTTAG_VILLAGE_HOMEBEDPOSITION, bedPosition.toLong());
					
					nbtTagListBedPositions.appendTag(nbtTagBedPosition);
				}
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_HOMEBEDPOSITIONS, nbtTagListBedPositions);
		}
		
		return nbtTag;
	}
	
}
