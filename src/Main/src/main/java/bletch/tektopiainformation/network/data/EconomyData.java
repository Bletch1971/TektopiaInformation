package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;

public class EconomyData {
	
	private static final String NBTTAG_VILLAGE_ECONOMY = "villageeconomy";
	private static final String NBTTAG_VILLAGE_PROFESSIONSALES = "villageprofessionsales";
	private static final String NBTTAG_VILLAGE_MERCHANTSALES = "villagemerchantsales";
	private static final String NBTTAG_VILLAGE_SALESHISTORY = "villagesaleshistory";
	
	private int professionSales;
	private int merchantSales;
	private List<ItemStack> salesHistory;

	public EconomyData() {
		populateData(null);
	}
	
	public EconomyData(Village village) {
		populateData(village);
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
	
	private void clearData() {
		this.professionSales = 0;
		this.merchantSales = 0;
		
		this.salesHistory = new ArrayList<ItemStack>();
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			
			// get the village town data
			IVillageData villageData = village.getTownData();
		
			this.professionSales = villageData != null ? villageData.getProfessionSales() : 0;
			this.merchantSales = villageData != null && villageData.getEconomy() != null ? villageData.getEconomy().getSalesHistorySize() : 0;
			
			if (villageData != null && villageData.getEconomy() != null) {
				List<ItemStack> salesHistory = TektopiaUtils.getEconomySalesHistory(villageData.getEconomy());
				
				for (ItemStack itemStack : salesHistory) {
					if (itemStack != null) {
						this.salesHistory.add(itemStack);
					}
				}
			}
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();

		if (nbtTag.hasKey(NBTTAG_VILLAGE_ECONOMY)) {
			NBTTagCompound nbtEconomyData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_ECONOMY);
			
			this.professionSales = nbtEconomyData.hasKey(NBTTAG_VILLAGE_PROFESSIONSALES) ? nbtEconomyData.getInteger(NBTTAG_VILLAGE_PROFESSIONSALES) : 0;
			this.merchantSales = nbtEconomyData.hasKey(NBTTAG_VILLAGE_MERCHANTSALES) ? nbtEconomyData.getInteger(NBTTAG_VILLAGE_MERCHANTSALES) : 0;
			
			if (nbtEconomyData.hasKey(NBTTAG_VILLAGE_SALESHISTORY)) {
				NBTTagList nbtTagListSalesHistory = nbtEconomyData.getTagList(NBTTAG_VILLAGE_SALESHISTORY, 10);
				
				for (int index = 0; index < nbtTagListSalesHistory.tagCount(); index++) {
					NBTTagCompound nbtTagSalesHistory = nbtTagListSalesHistory.getCompoundTagAt(index);
					
					this.salesHistory.add(new ItemStack(nbtTagSalesHistory));
				}
			}
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtEconomyData = new NBTTagCompound();

		nbtEconomyData.setInteger(NBTTAG_VILLAGE_PROFESSIONSALES, this.getProfessionSales());
		nbtEconomyData.setInteger(NBTTAG_VILLAGE_MERCHANTSALES, this.getMerchantSales());
		
		if (this.salesHistory != null && this.salesHistory.size() > 0) {
			NBTTagList nbtTagListSalesHistory = new NBTTagList();
			
			for (ItemStack itemStack : this.salesHistory) {
				if (itemStack != null) {
					NBTTagCompound nbtTagSalesHistory = new NBTTagCompound();
					nbtTagSalesHistory = itemStack.writeToNBT(nbtTagSalesHistory);
					
					nbtTagListSalesHistory.appendTag(nbtTagSalesHistory);
				}
			}
			
			nbtEconomyData.setTag(NBTTAG_VILLAGE_SALESHISTORY, nbtTagListSalesHistory);
		}

		nbtTag.setTag(NBTTAG_VILLAGE_ECONOMY, nbtEconomyData);
	}
	
}
