package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VisitorsData {
	
	private static final String NBTTAG_VILLAGE_VISITORS = "villagevisitors";
	private static final String NBTTAG_VILLAGE_VISITORSLIST = "villagevisitorslist";
	
	private List<VisitorData> visitors;

	public VisitorsData() {
		populateData(null);
	}
	
	public VisitorsData(Village village) {
		populateData(village);
	}
	
	public int getVisitorsCount() {
		return this.visitors == null
				? 0
				: this.visitors.size();
	}
	
	public List<VisitorData> getVisitors() {
		return this.visitors == null
				? Collections.unmodifiableList(new ArrayList<VisitorData>())
				: Collections.unmodifiableList(this.visitors.stream()
						.sorted((c1 , c2) -> {
							int compare = c1.getProfessionType().compareTo(c2.getProfessionType());
							return compare != 0 ? compare : c1.getName().compareTo(c2.getName());
						})
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
	
	private void clearData() {
		this.visitors = new ArrayList<VisitorData>();
	}
	
	public void populateData(Village village) {
		clearData();
		
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
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_VISITORS)) {
			NBTTagCompound nbtVisitorsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_VISITORS);
			
			if (nbtVisitorsData.hasKey(NBTTAG_VILLAGE_VISITORSLIST)) {
				NBTTagList nbtTagListVisitors = nbtVisitorsData.getTagList(NBTTAG_VILLAGE_VISITORSLIST, 10);
				
				for (int index = 0; index < nbtTagListVisitors.tagCount(); index++) {
					NBTTagCompound nbtTagVisitor = nbtTagListVisitors.getCompoundTagAt(index);
					
					this.visitors.add(new VisitorData(nbtTagVisitor));
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtVisitorsData = new NBTTagCompound();
		
		if (this.visitors != null) {
			NBTTagList nbtTagListVisitors = new NBTTagList();
			
			for (VisitorData visitor : this.visitors) {
				NBTTagCompound nbtVisitor = new NBTTagCompound();
				visitor.writeNBT(nbtVisitor);
				
				nbtTagListVisitors.appendTag(nbtVisitor);
			}
			
			nbtVisitorsData.setTag(NBTTAG_VILLAGE_VISITORSLIST, nbtTagListVisitors);
		}

		nbtTag.setTag(NBTTAG_VILLAGE_VISITORS, nbtVisitorsData);
	}

}
