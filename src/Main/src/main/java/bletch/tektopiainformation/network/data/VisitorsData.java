package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VisitorsData {
	
	private static final String NBTTAG_VILLAGE_VISITORS = "villagevisitors";
	private static final String NBTTAG_VILLAGE_VISITORSCOUNT = "villagevisitorscount";
	
	private int visitorsCount;
	private List<VisitorData> visitors;

	public VisitorsData() {
		populateData(null);
	}
	
	public VisitorsData(Village village) {
		populateData(village);
	}
	
	public int getVisitorsCount() {
		return this.visitorsCount;
	}
	
	public List<VisitorData> getVisitors() {
		return Collections.unmodifiableList(this.visitors == null ? new ArrayList<VisitorData>() : this.visitors.stream()
				.sorted((c1 , c2) -> c1.getProfessionType().compareTo(c2.getProfessionType()))
				.sorted((c1 , c2) -> c1.getName().compareTo(c2.getName()))
				.collect(Collectors.toList()));
	}
	
	public VisitorData getVisitor(int index) {
		return this.visitors == null ? null : this.visitors.get(index);
	}
	
	public VisitorData getVisitorById(int visitorId) {
		return this.visitors == null ? null : this.visitors.stream()
				.filter(m -> m.getId() == visitorId)
				.findFirst().orElse(null);
	}
	
	private void clearData() {
		this.visitorsCount = 0;
		
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
			
			this.visitorsCount = this.visitors.size();
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_VISITORS)) {
			NBTTagCompound nbtVisitorsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_VISITORS);
			
			this.visitorsCount = nbtVisitorsData.hasKey(NBTTAG_VILLAGE_VISITORSCOUNT) ? nbtVisitorsData.getInteger(NBTTAG_VILLAGE_VISITORSCOUNT) : 0;

			for (int visitorIndex = 0; visitorIndex < this.visitorsCount; visitorIndex++) {
				String key = getVisitorKey(visitorIndex);
				
				if (nbtVisitorsData.hasKey(key)) {
					VisitorData visitor = new VisitorData();
					visitor.readNBT(nbtVisitorsData.getCompoundTag(key));
					this.visitors.add(visitor);
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtVisitorsData = new NBTTagCompound();

		nbtVisitorsData.setInteger(NBTTAG_VILLAGE_VISITORSCOUNT, this.visitorsCount);
		
		if (this.visitors != null) {
			int visitorIndex = 0;
			for (VisitorData visitor : this.visitors) {
				NBTTagCompound nbtVisitorData = new NBTTagCompound();
				visitor.writeNBT(nbtVisitorData);
				nbtVisitorsData.setTag(getVisitorKey(visitorIndex++), nbtVisitorData);
			}
		}

		nbtTag.setTag(NBTTAG_VILLAGE_VISITORS, nbtVisitorsData);
	}

	public static String getVisitorKey(int visitorIndex) {
		return "visitor@" + visitorIndex;
	}

}
