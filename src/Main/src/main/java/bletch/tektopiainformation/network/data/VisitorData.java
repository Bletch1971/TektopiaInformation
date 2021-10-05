package bletch.tektopiainformation.network.data;

import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VisitorData extends ResidentData {
	
	public VisitorData() {
		super();
	}
	
	public VisitorData(EntityVillagerTek visitor) {
		super(visitor);
	}
	
	protected void populateData(EntityVillagerTek visitor) {
		super.populateData(visitor);
		
		if (visitor != null) {
			this.canHaveBed = false;
			
			String className = visitor.getClass().getSimpleName().toUpperCase();
			if (className.startsWith("ENTITY")) {
				this.professionType = className.substring("ENTITY".length());
			}
		}
	}
}
