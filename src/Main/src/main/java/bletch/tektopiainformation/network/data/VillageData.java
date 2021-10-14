package bletch.tektopiainformation.network.data;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class VillageData {

	private static final String NBTTAG_VILLAGE_NAME = "villagename";
	private static final String NBTTAG_VILLAGE_ORIGIN = "villageorigin";
	private static final String NBTTAG_VILLAGE_SIZE = "villagesize";

	private static final String NBTTAG_ENTITYID = "entityid";
	private static final String NBTTAG_BEDPOSITION = "bedposition";
	private static final String NBTTAG_FRAMEPOSITION = "frameposition";
	
	private static final String NBTTAG_PLAYERPOSITION = "playerposition";
	
	private String villageName;
	private BlockPos villageOrigin;
	private int villageSize;
	
	private StructuresData structuresData;
	private HomesData homesData;
	private ResidentsData residentsData;
	private EconomyData economyData;
	private VisitorsData visitorsData;
	private EnemiesData enemiesData;
	
	private int entityId;
	private int structureId;
	private BlockPos bedPosition;
	private BlockPos framePosition;
	
	private BlockPos playerPosition;
	
	public VillageData() {
		populateData(null);
		
		this.playerPosition = null;
	}
	
	public VillageData(Village village, BlockPos playerPosition) {
		populateData(village);
		
		this.playerPosition = playerPosition;
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
	
	public StructureData getTownHall() {
		return this.structuresData == null ? null : this.structuresData.getTownHall();
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

	public VisitorsData getVisitorsData() {
		return this.visitorsData == null ? new VisitorsData() : this.visitorsData;
	}

	public EnemiesData getEnemiesData() {
		return this.enemiesData == null ? new EnemiesData() : this.enemiesData;
	}
	
	public int getEntityId() {
		return this.entityId;
	}
	
	public int getStructureId() {
		return this.structureId;
	}
	
	public BlockPos getBedPosition() {
		return this.bedPosition;
	}
	
	public BlockPos getFramePosition() {
		return this.framePosition;
	}
	
	public BlockPos getPlayerPosition() {
		return this.playerPosition;
	}
	
	public VillageData setEntity(EntityVillageNavigator entity) {
		ClearAssignments();
		this.entityId = entity == null ? 0 : entity.getEntityId();
		return this;
	}
	
	public VillageData setEntityId(int entityId) {
		ClearAssignments();
		this.entityId = entityId;
		return this;
	}
	
	public VillageData setStructureId(int structureId) {
		ClearAssignments();
		this.structureId = structureId;
		return this;
	}
	
	public VillageData setBedPosition(BlockPos bedPosition) {
		ClearAssignments();
		this.bedPosition = bedPosition;
		return this;
	}
	
	public VillageData setFramePosition(BlockPos framePosition) {
		ClearAssignments();
		this.framePosition = framePosition;
		return this;
	}
	
	public void ClearAssignments() {
		this.entityId = 0;
		this.structureId = 0;
		this.bedPosition = null;
		this.framePosition = null;
	}
	
	private void clearData() {
		this.villageName = "";
		this.villageOrigin = null;
		this.villageSize = 0;
		
		ClearAssignments();
		
		this.playerPosition = null;
		
		this.structuresData = new StructuresData();
		this.homesData = new HomesData();
		this.residentsData = new ResidentsData();
		this.economyData = new EconomyData();
		this.visitorsData = new VisitorsData();
		this.enemiesData = new EnemiesData();
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
		this.visitorsData.populateData(village);
		this.enemiesData.populateData(village);
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
		
		this.entityId = nbtTag.hasKey(NBTTAG_ENTITYID) ? nbtTag.getInteger(NBTTAG_ENTITYID) : 0;
		this.bedPosition = nbtTag.hasKey(NBTTAG_BEDPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_BEDPOSITION)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_FRAMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_FRAMEPOSITION)) : null;
		
		this.playerPosition = nbtTag.hasKey(NBTTAG_PLAYERPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_PLAYERPOSITION)) : null;

		this.structuresData.readNBT(nbtTag);
		this.homesData.readNBT(nbtTag);
		this.residentsData.readNBT(nbtTag);
		this.economyData.readNBT(nbtTag);
		this.visitorsData.readNBT(nbtTag);
		this.enemiesData.readNBT(nbtTag);
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
		
		nbtTag.setInteger(NBTTAG_ENTITYID, this.entityId);
		if (this.bedPosition != null) {
			nbtTag.setLong(NBTTAG_BEDPOSITION, this.bedPosition.toLong());
		}
		if (this.framePosition != null) {
			nbtTag.setLong(NBTTAG_FRAMEPOSITION, this.framePosition.toLong());
		}
		
		if (this.playerPosition != null) {
			nbtTag.setLong(NBTTAG_PLAYERPOSITION, this.playerPosition.toLong());
		}		
		
		this.structuresData.writeNBT(nbtTag);
		this.homesData.writeNBT(nbtTag);
		this.residentsData.writeNBT(nbtTag);
		this.economyData.writeNBT(nbtTag);
		this.visitorsData.writeNBT(nbtTag);
		this.enemiesData.writeNBT(nbtTag);
	}
	
}
