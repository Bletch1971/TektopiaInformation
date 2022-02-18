package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class EnemiesData {
	
	protected static final String NBTTAG_VILLAGE_ENEMIES = "enemies";
	protected static final String NBTTAG_VILLAGE_ENEMIESLIST = "list";
	
	protected VillageData villageData;
	protected List<EnemyData> enemies;

	public EnemiesData() {
		populateData(null, null);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getEnemiesCount() {
		return this.enemies.size();
	}
	
	public List<EnemyData> getEnemies() {
		return this.enemies == null
				? Collections.unmodifiableList(new ArrayList<>())
				: Collections.unmodifiableList(this.enemies.stream()
						.sorted(Comparator.comparing(EntityData::getName))
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
	
	protected void clearData() {
		this.enemies = new ArrayList<>();
	}
	
	public void populateData(VillageData villageData, Village village) {
		clearData();
		
		this.villageData = villageData;
		
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
	
	public void readNBT(VillageData villageData, NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.villageData = villageData;
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_ENEMIES)) {
			NBTTagCompound nbtEnemiesData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_ENEMIES);
		
			if (nbtEnemiesData.hasKey(NBTTAG_VILLAGE_ENEMIESLIST)) {
				NBTTagList nbtTagListEnemies = nbtEnemiesData.getTagList(NBTTAG_VILLAGE_ENEMIESLIST, 10);
				
				for (int index = 0; index < nbtTagListEnemies.tagCount(); index++) {
					this.enemies.add(new EnemyData(nbtTagListEnemies.getCompoundTagAt(index)));
				}
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtEnemiesData = new NBTTagCompound();
		
		if (this.enemies != null && this.enemies.size() > 0) {
			NBTTagList nbtTagListEnemies = new NBTTagList();
			
			for (EnemyData enemy : this.enemies) {
				NBTTagCompound nbtTagEnemy = enemy.writeNBT(new NBTTagCompound());
				if (!nbtTagEnemy.hasNoTags()) {
					nbtTagListEnemies.appendTag(nbtTagEnemy);
				}
			}
			
			if (!nbtTagListEnemies.hasNoTags()) {
				nbtEnemiesData.setTag(NBTTAG_VILLAGE_ENEMIESLIST, nbtTagListEnemies);
			}
		}

		if (!nbtEnemiesData.hasNoTags()) {
			nbtTag.setTag(NBTTAG_VILLAGE_ENEMIES, nbtEnemiesData);
		}
		
		return nbtTag;
	}

}
