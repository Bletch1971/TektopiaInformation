package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VisitorsData {
	
	protected static final String NBTTAG_VILLAGE_VISITORS = "villagevisitors";
	protected static final String NBTTAG_VILLAGE_VISITORSLIST = "villagevisitorslist";

	protected VillageData villageData;
	protected List<VisitorData> visitors;

	public VisitorsData() {
		populateData(null, null);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getVisitorsCount() {
		return this.visitors == null
				? 0
				: this.visitors.size();
	}
	
	public List<VisitorData> getVisitors() {
		return this.visitors == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.visitors.stream()
						.sorted(Comparator.comparing((Function<VisitorData, String>) ResidentData::getProfessionType).thenComparing(EntityData::getName))
						.collect(Collectors.toList()));
	}
	
	public VisitorData getVisitor(int index) {
		return this.visitors == null 
				? null 
				: this.visitors.get(index);
	}
	
	public VisitorData getVisitorById(int visitorId) {
		return this.visitors == null 
				? null 
				: this.visitors.stream()
						.filter(m -> m.getId() == visitorId)
						.findFirst().orElse(null);
	}
	
	protected void clearData() {
		this.visitors = new ArrayList<>();
	}
	
	public void populateData(VillageData villageData, Village village) {
		clearData();
		
		this.villageData = villageData;
		
		if (village != null) {
			
			// get the visitor data
			AxisAlignedBB villageAABB = village.getAABB().grow(Village.VILLAGE_SIZE);
			List<EntityVillagerTek> villageEntities = village.getWorld().getEntitiesWithinAABB(EntityVillagerTek.class, villageAABB);
			
			for (EntityVillagerTek entity : villageEntities) {
				if (entity.isDead || !entity.isRole(VillagerRole.VISITOR) || entity.isRole(VillagerRole.ENEMY)) {
					continue;
				}
				
				this.visitors.add(new VisitorData(entity));
			}
		}
	}
	
	public void readNBT(VillageData villageData, NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.villageData = villageData;
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_VISITORS)) {
			NBTTagCompound nbtVisitorsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_VISITORS);
			
			if (nbtVisitorsData.hasKey(NBTTAG_VILLAGE_VISITORSLIST)) {
				NBTTagList nbtTagListVisitors = nbtVisitorsData.getTagList(NBTTAG_VILLAGE_VISITORSLIST, 10);
				
				for (int index = 0; index < nbtTagListVisitors.tagCount(); index++) {
					this.visitors.add(new VisitorData(nbtTagListVisitors.getCompoundTagAt(index)));
				}
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtVisitorsData = new NBTTagCompound();
		
		if (this.visitors != null) {
			NBTTagList nbtTagListVisitors = new NBTTagList();
			
			for (VisitorData visitor : this.visitors) {
				nbtTagListVisitors.appendTag(visitor.writeNBT(new NBTTagCompound()));
			}
			
			nbtVisitorsData.setTag(NBTTAG_VILLAGE_VISITORSLIST, nbtTagListVisitors);
		}

		nbtTag.setTag(NBTTAG_VILLAGE_VISITORS, nbtVisitorsData);
		
		return nbtTag;
	}

}
