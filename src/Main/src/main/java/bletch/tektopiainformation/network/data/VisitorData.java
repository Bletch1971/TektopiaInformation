package bletch.tektopiainformation.network.data;

import net.minecraft.nbt.NBTTagCompound;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VisitorData extends ResidentData {
	
	public VisitorData(EntityVillagerTek visitor) {
		super(visitor, false);
		
		populateData(visitor);
	}
	
	public VisitorData(NBTTagCompound nbtTag) {
		super();
		
		readNBT(nbtTag);
	}
	
	protected void clearData() {
		super.clearData();
	}
	
	protected void populateData(EntityVillagerTek villager) {
		super.populateData(villager);
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		super.readNBT(nbtTag);
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		nbtTag = super.writeNBT(nbtTag);
		
		return nbtTag;
	}
}
