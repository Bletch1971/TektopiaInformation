package bletch.tektopiainformation.network.data;

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

public class EnemyData extends EntityData {

	private static final String NBTTAG_VILLAGE_ENEMYTYPE = "villageenemytype";
	private static final String NBTTAG_VILLAGE_ENEMYCURRENTTASK = "villageenemycurrenttask";
	
	protected String enemyType;
	private String currentTask;
	
	public EnemyData() {
		super();
	}
	
	public EnemyData(EntityVillageNavigator enemy) {
		super(enemy, false);
		
		populateData(enemy);
	}
	
	public String getEnemyType() {
		return this.enemyType;
	}
	
	public String getCurrentTask() {
		return this.currentTask;
	}

	protected void clearData() {
		super.clearData();
		
		this.enemyType = null;
		this.currentTask = null;
	}
	
	protected void populateData(EntityVillageNavigator entity) {
		super.populateData(entity);
		
		if (entity != null) {
			String className = entity.getClass().getSimpleName().toUpperCase();
			if (className.startsWith("ENTITY")) {
				this.enemyType = className.substring("ENTITY".length());
			}
			
			if (entity instanceof EntityNecromancer) {
				this.level = ((EntityNecromancer)entity).getLevel();
			}
			
			for (EntityAITaskEntry task : entity.tasks.taskEntries) {
				if (task != null && task.action != null && task.using) {
					this.currentTask = task.action.getClass().getSimpleName();
				}
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		super.readNBT(nbtTag);

		this.enemyType = nbtTag.hasKey(NBTTAG_VILLAGE_ENEMYTYPE) ? nbtTag.getString(NBTTAG_VILLAGE_ENEMYTYPE) : null;
		this.currentTask = nbtTag.hasKey(NBTTAG_VILLAGE_ENEMYCURRENTTASK) ? nbtTag.getString(NBTTAG_VILLAGE_ENEMYCURRENTTASK) : null;
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		super.writeNBT(nbtTag);
		
		if (this.enemyType != null && this.enemyType.trim() != "") {
			nbtTag.setString(NBTTAG_VILLAGE_ENEMYTYPE, this.enemyType);
		}
		if (this.currentTask != null) {
			nbtTag.setString(NBTTAG_VILLAGE_ENEMYCURRENTTASK, this.currentTask);
		}
	}
}
