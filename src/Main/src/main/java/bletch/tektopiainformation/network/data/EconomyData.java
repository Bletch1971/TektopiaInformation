package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;

public class EconomyData {
	
	protected static final String NBTTAG_VILLAGE_ECONOMY = "villageeconomy";
	protected static final String NBTTAG_VILLAGE_PROFESSIONSALES = "villageprofessionsales";
	protected static final String NBTTAG_VILLAGE_MERCHANTSALES = "villagemerchantsales";
	protected static final String NBTTAG_VILLAGE_SALESHISTORY = "villagesaleshistory";

	protected VillageData villageData;
	protected int professionSales;
	protected int merchantSales;
	protected List<ItemStack> salesHistory;

	public EconomyData() {
		populateData(null, null);
	}
	
	protected VillageData getVillageData() {
		return this.villageData;
	}
	
	public int getProfessionSales() {
		return this.professionSales;
	}
	
	public int getMerchantSales() {
		return this.merchantSales;
	}
	
	public List<ItemStack> getSalesHistory() {
		return this.salesHistory == null
				? Collections.unmodifiableList(new ArrayList<ItemStack>())
				: Collections.unmodifiableList(this.salesHistory);
	}
	
	protected void clearData() {
		this.professionSales = 0;
		this.merchantSales = 0;
		
		this.salesHistory = new ArrayList<ItemStack>();
	}
	
	public void populateData(VillageData villageData, Village village) {
		clearData();
		
		this.villageData = villageData;
		
		if (village != null) {
			
			// get the village town data
			IVillageData townData = village.getTownData();
		
			this.professionSales = townData != null ? townData.getProfessionSales() : 0;
			this.merchantSales = townData != null && townData.getEconomy() != null ? townData.getEconomy().getSalesHistorySize() : 0;
			
			if (townData != null && townData.getEconomy() != null) {
				List<ItemStack> salesHistory = TektopiaUtils.getEconomySalesHistory(townData.getEconomy());
				
				for (ItemStack itemStack : salesHistory) {
					if (itemStack != null) {
						this.salesHistory.add(itemStack);
					}
				}
			}
		}
	}
	
	public void readNBT(VillageData villageData, NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		this.villageData = villageData;

		if (nbtTag.hasKey(NBTTAG_VILLAGE_ECONOMY)) {
			NBTTagCompound nbtEconomyData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_ECONOMY);
			
			this.professionSales = nbtEconomyData.hasKey(NBTTAG_VILLAGE_PROFESSIONSALES) ? nbtEconomyData.getInteger(NBTTAG_VILLAGE_PROFESSIONSALES) : 0;
			this.merchantSales = nbtEconomyData.hasKey(NBTTAG_VILLAGE_MERCHANTSALES) ? nbtEconomyData.getInteger(NBTTAG_VILLAGE_MERCHANTSALES) : 0;
			
			if (nbtEconomyData.hasKey(NBTTAG_VILLAGE_SALESHISTORY)) {
				NBTTagList nbtTagListSalesHistory = nbtEconomyData.getTagList(NBTTAG_VILLAGE_SALESHISTORY, 10);
				
				for (int index = 0; index < nbtTagListSalesHistory.tagCount(); index++) {
					this.salesHistory.add(new ItemStack(nbtTagListSalesHistory.getCompoundTagAt(index)));
				}
			}
		}
	}
	
	public NBTTagCompound writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtEconomyData = new NBTTagCompound();

		nbtEconomyData.setInteger(NBTTAG_VILLAGE_PROFESSIONSALES, this.getProfessionSales());
		nbtEconomyData.setInteger(NBTTAG_VILLAGE_MERCHANTSALES, this.getMerchantSales());
		
		if (this.salesHistory != null && this.salesHistory.size() > 0) {
			NBTTagList nbtTagListSalesHistory = new NBTTagList();

			for (ItemStack itemStack : this.salesHistory) {
				if (itemStack != null && !itemStack.isEmpty()) {
					nbtTagListSalesHistory.appendTag(itemStack.writeToNBT(new NBTTagCompound()));
				}
			}
			
			nbtEconomyData.setTag(NBTTAG_VILLAGE_SALESHISTORY, nbtTagListSalesHistory);
		}

		nbtTag.setTag(NBTTAG_VILLAGE_ECONOMY, nbtEconomyData);
		
		return nbtTag;
	}
	
}
