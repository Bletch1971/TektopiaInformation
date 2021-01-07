package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureBarracks;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureType;

public class HomeData {

	private static final String NBTTAG_VILLAGE_HOMEID = "villagehomeid";
	private static final String NBTTAG_VILLAGE_HOMETYPE = "villagehometype";
	private static final String NBTTAG_VILLAGE_HOMEPOSITION = "villagehomeposition";
	private static final String NBTTAG_VILLAGE_HOMEVALID = "villagehomevalid";
	private static final String NBTTAG_VILLAGE_HOMEFLOORTILECOUNT = "villagehomefloortilecount";
	private static final String NBTTAG_VILLAGE_HOMEMAXBEDS = "villagehomemaxbeds";
	private static final String NBTTAG_VILLAGE_HOMERESIDENTSCOUNT = "villagehomeresidentscount";
	private static final String NBTTAG_VILLAGE_HOMEFULL = "villagehomefull";
	private static final String NBTTAG_VILLAGE_HOMERESIDENTS = "villagehomeresidents";
	private static final String NBTTAG_VILLAGE_HOMEBEDPOSITIONCOUNT = "villagehomebedpositioncount";

	private UUID homeId;
	private VillageStructureType structureType;
	private BlockPos framePosition;
	private boolean isValid;
	private int floorTileCount;
	private int maxBeds;
	private int residentsCount;
	private boolean isFull;

	private List<ResidentData> residents;
	private List<BlockPos> bedPositions;
	
	public HomeData() {
		populateData(null);
	}
	
	public HomeData(VillageStructure structure) {
		populateData(structure);
	}
	
	public UUID getHomeId() {
		return this.homeId;
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
	
	public int getMaxBeds() {
		return this.maxBeds;
	}
	
	public int getResidentsCount() {
		return this.residentsCount;
	}
	
	public boolean isFull() {
		return this.isFull;
	}
	
	public List<ResidentData> getResidents() {
		return Collections.unmodifiableList(this.residents != null ? this.residents : new ArrayList<ResidentData>());
	}
	
	public ResidentData getResidentByBedPosition(BlockPos bedPosition) {
		return this.residents == null ? null : this.residents.stream()
				.filter(r -> bedPosition != null && bedPosition.equals(r.getBedPosition()))
				.findFirst().orElse(null);
	}
	
	public int getBedCount() {
		return this.bedPositions != null ? this.bedPositions.size() : 0;
	}
	
	public List<BlockPos> getBedPositions() {
		return this.bedPositions == null ? new ArrayList<BlockPos>() : Collections.unmodifiableList(this.bedPositions.stream()
				.sorted((c1 , c2) -> c1.compareTo(c2))
				.collect(Collectors.toList()));
	}
	
	public boolean hasBedPosition(BlockPos bedPosition) {
		return this.bedPositions != null && bedPosition != null && this.bedPositions.stream()
				.anyMatch(p -> bedPosition.equals(p));
	}
	
	public int getAdultCount() {
		return this.residents == null ? 0 : (int)this.residents.stream()
				.filter(r -> !r.isChild())
				.count();
	}
	
	public int getChildCount() {
		return this.residents == null ? 0 : (int)this.residents.stream()
				.filter(r -> r.isChild())
				.count();
	}
	
	public int getMaleCount() {
		return this.residents == null ? 0 : (int)this.residents.stream()
				.filter(r -> r.isMale())
				.count();
	}
	
	public int getFemaleCount() {
		return this.residents == null ? 0 : (int)this.residents.stream()
				.filter(r -> !r.isMale())
				.count();
	}
	
	private void clearData() {
		this.homeId = UUID.randomUUID();
		this.structureType = null;
		this.framePosition = null;
		this.isValid = false;
		this.floorTileCount = 0;
		this.maxBeds = 0;
		this.residentsCount = 0;
		this.isFull = this.residentsCount >= this.maxBeds;
		
		this.residents = new ArrayList<ResidentData>();
		this.bedPositions = new ArrayList<BlockPos>();
	}
	
	public void populateData(VillageStructure structure) {
		clearData();
		
		List<VillageStructureType> homeTypes = TektopiaUtils.getHomeStructureTypes();

		if (structure != null && homeTypes != null && homeTypes.contains(structure.type)) {
			this.structureType = structure.type;
			this.framePosition = structure.getFramePos();
			this.isValid = structure.isValid();
			
			List<BlockPos> floorTiles = TektopiaUtils.getStructureFloorTiles(structure);
			this.floorTileCount = floorTiles == null ? 0 : floorTiles.size();
			
			if (structure instanceof VillageStructureBarracks) {
				VillageStructureBarracks barracks = (VillageStructureBarracks)structure;
				
				this.maxBeds = TektopiaUtils.getStructureMaxBeds(barracks);
				this.residentsCount = barracks.getCurResidents();
				this.isFull = barracks.isFull();
				
				for (EntityVillagerTek resident : barracks.getResidents()) {
					this.residents.add(new ResidentData(resident));
				}

				this.bedPositions = new ArrayList<BlockPos>(barracks.getSpecialBlocks(Blocks.BED));
			}
			else if (structure instanceof VillageStructureHome) {
				VillageStructureHome home = (VillageStructureHome)structure;
				
				this.maxBeds = TektopiaUtils.getStructureMaxBeds(home);
				this.residentsCount = home.getCurResidents();
				this.isFull = home.isFull();
				
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
		
		this.homeId = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEID) ? nbtTag.getUniqueId(NBTTAG_VILLAGE_HOMEID) : UUID.randomUUID();
		this.structureType = nbtTag.hasKey(NBTTAG_VILLAGE_HOMETYPE) ? VillageStructureType.valueOf(nbtTag.getString(NBTTAG_VILLAGE_HOMETYPE)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_HOMEPOSITION)) : null;
		this.isValid = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEVALID) ? nbtTag.getBoolean(NBTTAG_VILLAGE_HOMEVALID) : false;
		this.floorTileCount = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEFLOORTILECOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMEFLOORTILECOUNT) : 0;

		this.maxBeds = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEMAXBEDS) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMEMAXBEDS) : 0;
		this.residentsCount = nbtTag.hasKey(NBTTAG_VILLAGE_HOMERESIDENTSCOUNT) ? nbtTag.getInteger(NBTTAG_VILLAGE_HOMERESIDENTSCOUNT) : 0;
		this.isFull = nbtTag.hasKey(NBTTAG_VILLAGE_HOMEFULL) ? nbtTag.getBoolean(NBTTAG_VILLAGE_HOMEFULL) : false;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_HOMERESIDENTS)) {
			NBTTagCompound nbtResidentsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_HOMERESIDENTS);

			for (int residentIndex = 0; residentIndex < this.residentsCount; residentIndex++) {
				String key = getResidentKey(residentIndex);
				
				if (nbtResidentsData.hasKey(key)) {
					ResidentData resident = new ResidentData();
					resident.readNBT(nbtResidentsData.getCompoundTag(key));
					this.residents.add(resident);
				}
			}
		}
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_HOMEBEDPOSITIONCOUNT)) {
			int bedCount = nbtTag.getInteger(NBTTAG_VILLAGE_HOMEBEDPOSITIONCOUNT);
			
			for (int bedIndex = 0; bedIndex < bedCount; bedIndex++) {
				if (nbtTag.hasKey(getBedPositionKey(bedIndex))) {
					this.bedPositions.add(BlockPos.fromLong(nbtTag.getLong(getBedPositionKey(bedIndex))));
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}

		nbtTag.setUniqueId(NBTTAG_VILLAGE_HOMEID, this.homeId);
		if (this.structureType != null) {
			nbtTag.setString(NBTTAG_VILLAGE_HOMETYPE, this.structureType.name());
		}
		if (this.framePosition != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_HOMEPOSITION, this.framePosition.toLong());
		}
		nbtTag.setBoolean(NBTTAG_VILLAGE_HOMEVALID, this.isValid);
		nbtTag.setInteger(NBTTAG_VILLAGE_HOMEFLOORTILECOUNT, this.floorTileCount);

		nbtTag.setInteger(NBTTAG_VILLAGE_HOMEMAXBEDS, this.maxBeds);
		nbtTag.setInteger(NBTTAG_VILLAGE_HOMERESIDENTSCOUNT, this.residentsCount);
		nbtTag.setBoolean(NBTTAG_VILLAGE_HOMEFULL, this.isFull);
		
		if (this.residents != null) {
			NBTTagCompound nbtResidentsData = new NBTTagCompound();
			
			int residentIndex = 0;
			for (ResidentData resident : this.residents) {
				NBTTagCompound nbtResidentData = new NBTTagCompound();
				resident.writeNBT(nbtResidentData);
				nbtResidentsData.setTag(getResidentKey(residentIndex++), nbtResidentData);
			}
			
			nbtTag.setTag(NBTTAG_VILLAGE_HOMERESIDENTS, nbtResidentsData);
		}
		
		if (this.bedPositions != null) {
			nbtTag.setInteger(NBTTAG_VILLAGE_HOMEBEDPOSITIONCOUNT, this.bedPositions.size());
			
			int bedIndex = 0;
			for (BlockPos bedPosition : this.bedPositions) {
				if (bedPosition != null) {
					nbtTag.setLong(getBedPositionKey(bedIndex), bedPosition.toLong());
					bedIndex++;
				}
			}
		}
	}

	public static String getBedPositionKey(int bedIndex) {
		return "bed@" + bedIndex;
	}
	
	public static String getResidentKey(int residentIndex) {
		return "resident@" + residentIndex;
	}
	
}
