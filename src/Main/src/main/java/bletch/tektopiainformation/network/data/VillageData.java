package bletch.tektopiainformation.network.data;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VillageData {

	private static final String NBTTAG_VILLAGE_NAME = "villagename";
	private static final String NBTTAG_VILLAGE_ORIGIN = "villageorigin";
	private static final String NBTTAG_VILLAGE_SIZE = "villagesize";

	private static final String NBTTAG_RESIDENTID = "residentid";
	private static final String NBTTAG_BEDPOSITION = "bedposition";
	private static final String NBTTAG_FRAMEPOSITION = "frameposition";
	
	private String villageName;
	private BlockPos villageOrigin;
	private int villageSize;
	
	private StructuresData structuresData;
	private HomesData homesData;
	private ResidentsData residentsData;
	private EconomyData economyData;
	
	private int residentId;
	private BlockPos bedPosition;
	private BlockPos framePosition;
	
	public VillageData() {
		populateData(null);
	}
	
	public VillageData(Village village) {
		populateData(village);
	}
	
	public String getVillageName() {
		return this.villageName;
	}
	
	public BlockPos getVillageOrigin() {
		return this.villageOrigin;
	}
	
	public int getVillageSize() {
		return this.villageSize;
	}
	
	public BlockPos getVillageNorthWestCorner() {
		return this.villageOrigin != null ? this.villageOrigin.north(this.villageSize).west(this.villageSize) : null;
	}
	
	public BlockPos getVillageNorthEastCorner() {
		return this.villageOrigin != null ? this.villageOrigin.north(this.villageSize).east(this.villageSize) : null;
	}
	
	public BlockPos getVillageSouthWestCorner() {
		return this.villageOrigin != null ? this.villageOrigin.south(this.villageSize).west(this.villageSize) : null;
	}
	
	public BlockPos getVillageSouthEastCorner() {
		return this.villageOrigin != null ? this.villageOrigin.south(this.villageSize).east(this.villageSize) : null;
	}
	
	public StructuresData getStructuresData() {
		return this.structuresData == null ? new StructuresData() : this.structuresData;
	}	

	public HomesData getHomesData() {
		return this.structuresData == null ? new HomesData() : this.homesData;
	}
	
	public ResidentsData getResidentsData() {
		return this.structuresData == null ? new ResidentsData() : this.residentsData;
	}

	public EconomyData getEconomyData() {
		return this.structuresData == null ? new EconomyData() : this.economyData;
	}	
	
	public int getResidentId() {
		return this.residentId;
	}
	
	public BlockPos getBedPosition() {
		return this.bedPosition;
	}
	
	public BlockPos getFramePosition() {
		return this.framePosition;
	}
	
	public VillageData setResident(EntityVillagerTek villager) {
		this.residentId = villager == null ? 0 : villager.getEntityId();
		return this;
	}
	
	public VillageData setBedPosition(BlockPos bedPosition) {
		this.bedPosition = bedPosition;
		return this;
	}
	
	public VillageData setFramePosition(BlockPos framePosition) {
		this.framePosition = framePosition;
		return this;
	}
	
	private void clearData() {
		this.villageName = "";
		this.villageOrigin = null;
		this.villageSize = 0;
		
		this.residentId = 0;
		this.bedPosition = null;
		this.framePosition = null;
		
		this.structuresData = new StructuresData();
		this.homesData = new HomesData();
		this.residentsData = new ResidentsData();
		this.economyData = new EconomyData();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			this.villageName = village.getName();
			this.villageOrigin = village.getOrigin();
			this.villageSize = village.getSize();
		}
		
		this.structuresData.populateData(village);
		this.homesData.populateData(village);
		this.residentsData.populateData(village);
		this.economyData.populateData(village);
	}
	
	public void readBuffer(PacketBuffer buffer) throws IOException {
		if (buffer == null) {
			return;
		}
		
		NBTTagCompound nbtTag = buffer.readCompoundTag();
		readNBT(nbtTag);
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		ResidentData.resetEntityList();
		
		this.villageName = nbtTag.hasKey(NBTTAG_VILLAGE_NAME) ? nbtTag.getString(NBTTAG_VILLAGE_NAME) : "";
		this.villageOrigin = nbtTag.hasKey(NBTTAG_VILLAGE_ORIGIN) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_VILLAGE_ORIGIN)) : null;
		this.villageSize = nbtTag.hasKey(NBTTAG_VILLAGE_SIZE) ? nbtTag.getInteger(NBTTAG_VILLAGE_SIZE) : 0;
		
		this.residentId = nbtTag.hasKey(NBTTAG_RESIDENTID) ? nbtTag.getInteger(NBTTAG_RESIDENTID) : 0;
		this.bedPosition = nbtTag.hasKey(NBTTAG_BEDPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_BEDPOSITION)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_FRAMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_FRAMEPOSITION)) : null;

		this.structuresData.readNBT(nbtTag);
		this.homesData.readNBT(nbtTag);
		this.residentsData.readNBT(nbtTag);
		this.economyData.readNBT(nbtTag);
	}
	
	public void writeBuffer(PacketBuffer buffer) throws IOException {
		if (buffer == null) {
			return;
		}
		
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeNBT(nbtTag);
		buffer.writeCompoundTag(nbtTag);
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		nbtTag.setString(NBTTAG_VILLAGE_NAME, this.villageName);
		if (this.villageOrigin != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_ORIGIN, this.villageOrigin.toLong());
		}
		nbtTag.setInteger(NBTTAG_VILLAGE_SIZE, this.villageSize);
		
		nbtTag.setInteger(NBTTAG_RESIDENTID, this.residentId);
		if (this.bedPosition != null) {
			nbtTag.setLong(NBTTAG_BEDPOSITION, this.bedPosition.toLong());
		}
		if (this.framePosition != null) {
			nbtTag.setLong(NBTTAG_FRAMEPOSITION, this.framePosition.toLong());
		}		
		this.structuresData.writeNBT(nbtTag);
		this.homesData.writeNBT(nbtTag);
		this.residentsData.writeNBT(nbtTag);
		this.economyData.writeNBT(nbtTag);
	}
	
}
