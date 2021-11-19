package bletch.tektopiainformation.network.data;

import java.io.IOException;

import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.utils.LoggerUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class VillageData {
	public static final float MC_TICKS_PER_SECOND = 1000.0F / 60.0F / 60.0F;

	protected static final String NBTTAG_VILLAGE_NAME = "villagename";
	protected static final String NBTTAG_VILLAGE_ORIGIN = "villageorigin";
	protected static final String NBTTAG_VILLAGE_SIZE = "villagesize";
	protected static final String NBTTAG_VILLAGE_WORLDTIME = "villageworldtime";

	protected static final String NBTTAG_ENTITYID = "entityid";
	protected static final String NBTTAG_BEDPOSITION = "bedposition";
	protected static final String NBTTAG_FRAMEPOSITION = "frameposition";
	
	protected static final String NBTTAG_PLAYERPOSITION = "playerposition";
	
	protected String villageName;
	protected BlockPos villageOrigin;
	protected int villageSize;
	protected long worldTime;
	
	protected StructuresData structuresData;
	protected HomesData homesData;
	protected ResidentsData residentsData;
	protected EconomyData economyData;
	protected VisitorsData visitorsData;
	protected EnemiesData enemiesData;
	
	protected int entityId;
	protected int structureId;
	protected BlockPos bedPosition;
	protected BlockPos framePosition;
	
	protected BlockPos playerPosition;
	
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
	
	public long getVillageDays() {
		return (long) Math.max(1.0F, ((float)this.worldTime / 24000.0F) + 1.0F);
	}
	
	public long getVillageSeconds() {
		return (long) Math.max(0.0F, ((float)this.worldTime % 24000.0F) / MC_TICKS_PER_SECOND);
	}
	
	public long getVillageTicks() {
		return (long) Math.max(0.0F, (float)this.worldTime % 24000.0F);
	}
	
	public long getWorldTime() {
		return this.worldTime;
	}
	
	public BlockPos getVillageNorthWestCorner() {
		return this.villageOrigin != null 
				? this.villageOrigin.north(this.villageSize).west(this.villageSize) 
				: null;
	}
	
	public BlockPos getVillageNorthEastCorner() {
		return this.villageOrigin != null 
				? this.villageOrigin.north(this.villageSize).east(this.villageSize) 
				: null;
	}
	
	public BlockPos getVillageSouthWestCorner() {
		return this.villageOrigin != null 
				? this.villageOrigin.south(this.villageSize).west(this.villageSize) 
				: null;
	}
	
	public BlockPos getVillageSouthEastCorner() {
		return this.villageOrigin != null 
				? this.villageOrigin.south(this.villageSize).east(this.villageSize) 
				: null;
	}
	
	public StructureData getTownHall() {
		return this.structuresData == null 
				? null 
				: this.structuresData.getTownHall();
	}
	
	public StructuresData getStructuresData() {
		return this.structuresData == null 
				? new StructuresData() 
				: this.structuresData;
	}	

	public HomesData getHomesData() {
		return this.structuresData == null 
				? new HomesData() 
				: this.homesData;
	}
	
	public ResidentsData getResidentsData() {
		return this.structuresData == null 
				? new ResidentsData() 
				: this.residentsData;
	}

	public EconomyData getEconomyData() {
		return this.structuresData == null 
				? new EconomyData() 
				: this.economyData;
	}

	public VisitorsData getVisitorsData() {
		return this.visitorsData == null 
				? new VisitorsData() 
				: this.visitorsData;
	}

	public EnemiesData getEnemiesData() {
		return this.enemiesData == null 
				? new EnemiesData() 
				: this.enemiesData;
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
	
	protected void clearData() {
		this.villageName = "";
		this.villageOrigin = null;
		this.villageSize = 0;
		this.worldTime = -1;
		
		ClearAssignments();
		
		this.playerPosition = null;
		
		this.residentsData = new ResidentsData();
		this.visitorsData = new VisitorsData();
		this.enemiesData = new EnemiesData();
		
		this.structuresData = new StructuresData();
		this.homesData = new HomesData();
		this.economyData = new EconomyData();
	}
	
	protected void populateData(Village village) {
		clearData();
		
		if (village != null) {
			this.villageName = village.getName();
			this.villageOrigin = village.getOrigin();
			this.villageSize = village.getSize();
			
			this.worldTime = village.getWorld().getWorldTime();
		}
		
		this.structuresData.populateData(this, village);
		this.homesData.populateData(this, village);
		this.residentsData.populateData(this, village);
		this.economyData.populateData(this, village);
		this.visitorsData.populateData(this, village);
		
		if (ModConfig.gui.tektopiaInformationBook.showEnemies)
			this.enemiesData.populateData(this, village);
	}
	
	public void readBuffer(PacketBuffer buffer) throws IOException {
		if (buffer == null) {
			return;
		}
		
		String logMessage = "VillageData - readBuffer called; buffer capacity=" + buffer.capacity() + "; buffer max capacity=" + buffer.maxCapacity();
		LoggerUtils.instance.info(logMessage, true);
		
		readNBT(buffer.readCompoundTag());
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
		this.worldTime = nbtTag.hasKey(NBTTAG_VILLAGE_WORLDTIME) ? nbtTag.getLong(NBTTAG_VILLAGE_WORLDTIME) : -1;
		
		this.entityId = nbtTag.hasKey(NBTTAG_ENTITYID) ? nbtTag.getInteger(NBTTAG_ENTITYID) : 0;
		this.bedPosition = nbtTag.hasKey(NBTTAG_BEDPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_BEDPOSITION)) : null;
		this.framePosition = nbtTag.hasKey(NBTTAG_FRAMEPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_FRAMEPOSITION)) : null;
		
		this.playerPosition = nbtTag.hasKey(NBTTAG_PLAYERPOSITION) ? BlockPos.fromLong(nbtTag.getLong(NBTTAG_PLAYERPOSITION)) : null;

		this.residentsData.readNBT(this, nbtTag);
		this.visitorsData.readNBT(this, nbtTag);
		this.enemiesData.readNBT(this, nbtTag);
		this.structuresData.readNBT(this, nbtTag);
		this.homesData.readNBT(this, nbtTag);
		this.economyData.readNBT(this, nbtTag);
	}
	
	public void writeBuffer(PacketBuffer buffer) {
		if (buffer == null) {
			return;
		}
		
		NBTTagCompound nbtTag = writeNBT(new NBTTagCompound());
		buffer.writeCompoundTag(nbtTag);
		
		String logMessage = "VillageData - writeBuffer called; buffer capacity=" + buffer.capacity() + "; buffer max capacity=" + buffer.maxCapacity();
		LoggerUtils.instance.info(logMessage, true);
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		nbtTag.setString(NBTTAG_VILLAGE_NAME, this.villageName);
		if (this.villageOrigin != null) {
			nbtTag.setLong(NBTTAG_VILLAGE_ORIGIN, this.villageOrigin.toLong());
		}
		nbtTag.setInteger(NBTTAG_VILLAGE_SIZE, this.villageSize);
		nbtTag.setLong(NBTTAG_VILLAGE_WORLDTIME, this.worldTime);
		
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

		this.residentsData.writeNBT(nbtTag);
		this.visitorsData.writeNBT(nbtTag);
		this.enemiesData.writeNBT(nbtTag);
		this.structuresData.writeNBT(nbtTag);
		this.homesData.writeNBT(nbtTag);
		this.economyData.writeNBT(nbtTag);
		
		return nbtTag;
	}
	
}
