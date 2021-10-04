package bletch.tektopiainformation.network.data;

import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class EnemyData extends EntityData {

	public EnemyData() {
		super();
	}
	
	public EnemyData(EntityVillageNavigator enemy) {
		populateData(enemy);
	}

}
