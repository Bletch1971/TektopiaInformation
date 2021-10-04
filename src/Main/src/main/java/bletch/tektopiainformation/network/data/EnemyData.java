package bletch.tektopiainformation.network.data;

import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class EnemyData extends EntityData {

	public EnemyData() {
		populateData(null);
	}
	
	public EnemyData(EntityVillageNavigator enemy) {
		populateData(enemy);
	}

	protected void populateData(EntityVillageNavigator entity) {
		super.populateData(entity);
		
		if (entity instanceof EntityNecromancer) {
			this.level = ((EntityNecromancer)entity).getLevel();
		}
	}
}
