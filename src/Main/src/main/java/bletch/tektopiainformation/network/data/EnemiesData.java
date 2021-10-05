package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class EnemiesData {
	
	private static final String NBTTAG_VILLAGE_ENEMIES = "villageenemies";
	private static final String NBTTAG_VILLAGE_ENEMIESCOUNT = "villageenemiescount";
	
	private int enemiesCount;
	private List<EnemyData> enemies;

	public EnemiesData() {
		populateData(null);
	}
	
	public EnemiesData(Village village) {
		populateData(village);
	}
	
	public int getEnemiesCount() {
		return this.enemiesCount;
	}
	
	public List<EnemyData> getEnemies() {
		return Collections.unmodifiableList(this.enemies == null ? new ArrayList<EnemyData>() : this.enemies.stream()
				.sorted((c1 , c2) -> c1.getName().compareTo(c2.getName()))
				.collect(Collectors.toList()));
	}
	
	public EnemyData getEnemy(int index) {
		return this.enemies == null ? null : this.enemies.get(index);
	}
	
	public EnemyData getEnemyById(int id) {
		return this.enemies == null ? null : this.enemies.stream()
				.filter(m -> m.getId() == id)
				.findFirst().orElse(null);
	}
	
	private void clearData() {
		this.enemiesCount = 0;
		
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
			
			this.enemiesCount = this.enemies.size();
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENEMIES)) {
			NBTTagCompound nbtEnemiesData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_ENEMIES);
			
			this.enemiesCount = nbtEnemiesData.hasKey(NBTTAG_VILLAGE_ENEMIESCOUNT) ? nbtEnemiesData.getInteger(NBTTAG_VILLAGE_ENEMIESCOUNT) : 0;

			for (int enemyIndex = 0; enemyIndex < this.enemiesCount; enemyIndex++) {
				String key = getEnemyKey(enemyIndex);
				
				if (nbtEnemiesData.hasKey(key)) {
					EnemyData enemy = new EnemyData();
					enemy.readNBT(nbtEnemiesData.getCompoundTag(key));
					this.enemies.add(enemy);
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtEnemiesData = new NBTTagCompound();

		nbtEnemiesData.setInteger(NBTTAG_VILLAGE_ENEMIESCOUNT, this.enemiesCount);
		
		if (this.enemies != null) {
			int enemyIndex = 0;
			for (EnemyData enemy : this.enemies) {
				NBTTagCompound nbtEnemyData = new NBTTagCompound();
				enemy.writeNBT(nbtEnemyData);
				nbtEnemiesData.setTag(getEnemyKey(enemyIndex++), nbtEnemyData);
			}
		}

		nbtTag.setTag(NBTTAG_VILLAGE_ENEMIES, nbtEnemiesData);
	}

	public static String getEnemyKey(int enemyIndex) {
		return "enemy@" + enemyIndex;
	}

}
