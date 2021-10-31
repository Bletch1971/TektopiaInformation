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
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class EnemiesData {
	
	private static final String NBTTAG_VILLAGE_ENEMIES = "villageenemies";
	private static final String NBTTAG_VILLAGE_ENEMIESLIST = "villageenemieslist";
	
	private List<EnemyData> enemies;

	public EnemiesData() {
		populateData(null);
	}
	
	public EnemiesData(Village village) {
		populateData(village);
	}
	
	public int getEnemiesCount() {
		return this.enemies.size();
	}
	
	public List<EnemyData> getEnemies() {
		return this.enemies == null
				? Collections.unmodifiableList(new ArrayList<EnemyData>())
				: Collections.unmodifiableList(this.enemies.stream()
						.sorted((c1 , c2) -> c1.getName().compareTo(c2.getName()))
						.collect(Collectors.toList()));
	}
	
	public EnemyData getEnemy(int index) {
		return this.enemies == null ? null : this.enemies.get(index);
	}
	
	public EnemyData getEnemyById(int id) {
		return this.enemies == null 
				? null 
				: this.enemies.stream()
						.filter(m -> m.getId() == id)
						.findFirst().orElse(null);
	}
	
	private void clearData() {
		this.enemies = new ArrayList<EnemyData>();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			
			// get the enemy data
			AxisAlignedBB villageAABB = village.getAABB().grow(Village.VILLAGE_SIZE);
			List<EntityVillageNavigator> villageEntities = village.getWorld().getEntitiesWithinAABB(EntityVillageNavigator.class, villageAABB);
			
			for (EntityVillageNavigator entity : villageEntities) { 
				if (entity.isDead || !entity.isRole(VillagerRole.ENEMY)) {
					continue;
				}
				
				this.enemies.add(new EnemyData(entity));
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENEMIES)) {
			NBTTagCompound nbtEnemiesData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_ENEMIES);
		
			if (nbtEnemiesData.hasKey(NBTTAG_VILLAGE_ENEMIESLIST)) {
				NBTTagList nbtTagListEnemies = nbtEnemiesData.getTagList(NBTTAG_VILLAGE_ENEMIESLIST, 10);
				
				for (int index = 0; index < nbtTagListEnemies.tagCount(); index++) {
					NBTTagCompound nbtTagEnemy = nbtTagListEnemies.getCompoundTagAt(index);
					
					this.enemies.add(new EnemyData(nbtTagEnemy));
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtEnemiesData = new NBTTagCompound();
		
		if (this.enemies != null) {
			NBTTagList nbtTagListEnemies = new NBTTagList();
			
			for (EnemyData enemy : this.enemies) {
				NBTTagCompound nbtEnemy = new NBTTagCompound();
				enemy.writeNBT(nbtEnemy);
				
				nbtTagListEnemies.appendTag(nbtEnemy);
			}
			
			nbtEnemiesData.setTag(NBTTAG_VILLAGE_ENEMIESLIST, nbtTagListEnemies);
		}

		nbtTag.setTag(NBTTAG_VILLAGE_ENEMIES, nbtEnemiesData);
	}

}
