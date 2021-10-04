package bletch.tektopiainformation.network.data;

import net.minecraft.entity.IMerchant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipeList;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VisitorData extends ResidentData {

	private static final String NBTTAG_VILLAGE_VISITORRECIPES = "villagevisitorrecipes";
	
	private MerchantRecipeList recipes;
	
	public VisitorData() {
		super();
	}
	
	public VisitorData(EntityVillagerTek visitor) {
		super(visitor);
	}
	
	public MerchantRecipeList getRecipeList() {
		return this.recipes;
	}

	protected void clearData() {
		super.clearData();
		
		this.recipes = new MerchantRecipeList();
	}
	
	protected void populateData(EntityVillagerTek visitor) {
		clearData();
		
		super.populateData(visitor);
		
		if (visitor instanceof IMerchant) {
			this.recipes = ((IMerchant)visitor).getRecipes(null);
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		super.readNBT(nbtTag);
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_VISITORRECIPES)) {
			this.recipes.readRecipiesFromTags(nbtTag.getCompoundTag(NBTTAG_VILLAGE_VISITORRECIPES));
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		super.writeNBT(nbtTag);
		
		if (this.recipes != null && !this.recipes.isEmpty()) {
			nbtTag.setTag(NBTTAG_VILLAGE_VISITORRECIPES, this.recipes.getRecipiesAsTags());
		}
	}
}
