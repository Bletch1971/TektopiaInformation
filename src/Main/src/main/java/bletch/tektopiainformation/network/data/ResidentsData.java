package bletch.tektopiainformation.network.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ResidentsData {
	
	private static final String NBTTAG_VILLAGE_RESIDENTS = "villageresidents";
	private static final String NBTTAG_VILLAGE_RESIDENTSCOUNT = "villageresidentscount";
	private static final String NBTTAG_VILLAGE_RESIDENTADULTCOUNT = "villageresidentadultcount";
	private static final String NBTTAG_VILLAGE_RESIDENTCHILDCOUNT = "villageresidentchildcount";
	private static final String NBTTAG_VILLAGE_RESIDENTMALECOUNT = "villageresidentmalecount";
	private static final String NBTTAG_VILLAGE_RESIDENTFEMALECOUNT = "villageresidentfemalecount";
		
	public static final int STATISTICS_RANGE = 20;
	
	private int residentsCount;
	private List<ResidentData> residents;
	private Map<ProfessionType, Integer> professionTypeCounts;

	private int adultCount = 0;
	private int childCount = 0;
	private int maleCount = 0;
	private int femaleCount = 0;

	public ResidentsData() {
		populateData(null);
	}
	
	public ResidentsData(Village village) {
		populateData(village);
	}
	
	public int getResidentsCount() {
		return this.residentsCount;
	}
	
	public List<ResidentData> getResidents() {
		return Collections.unmodifiableList(this.residents == null ? new ArrayList<ResidentData>() : this.residents.stream()
				.sorted((c1 , c2) -> c1.getResidentName().compareTo(c2.getResidentName()))
				.collect(Collectors.toList()));
	}
	
	public List<ResidentData> getResidentsByType(ProfessionType professionType) {
		return Collections.unmodifiableList(this.residents == null ? new ArrayList<ResidentData>() : this.residents.stream()
				.filter(r -> professionType != null && professionType.equals(r.getProfessionType()))
				.sorted((c1 , c2) -> { 
					int compare = Integer.compare(c2.getBaseLevel(), c1.getBaseLevel());
					if (compare == 0) {
						compare = c1.getResidentName().compareTo(c2.getResidentName());
					}
					return compare;
				})
				.collect(Collectors.toList()));
	}
	
	public ResidentData getResident(int index) {
		return this.residents == null ? null : this.residents.get(index);
	}
	
	public ResidentData getResidentById(int residentId) {
		return this.residents == null ? null : this.residents.stream()
				.filter(r -> r.getResidentId() == residentId)
				.findFirst().orElse(null);
	}
	
	public ResidentData getResidentByBedPosition(BlockPos bedPosition) {
		return this.residents == null ? null : this.residents.stream()
				.filter(r -> bedPosition != null && bedPosition.equals(r.getBedPosition()))
				.findFirst().orElse(null);
	}
	
	public Map<ProfessionType, Integer> getProfessionTypeCounts() {
		return Collections.unmodifiableMap(this.professionTypeCounts == null ? new HashMap<ProfessionType, Integer>() : this.professionTypeCounts);
	}	
	
	public int getProfessionTypeCount(ProfessionType professionType) {
		return professionType != null && this.professionTypeCounts != null && this.professionTypeCounts.containsKey(professionType) ? this.professionTypeCounts.get(professionType) : 0;
	}
	
	public int getAdultCount() {
		return this.adultCount;
	}
	
	public int getChildCount() {
		return this.childCount;
	}
	
	public int getMaleCount() {
		return this.maleCount;
	}
	
	public int getFemaleCount() {
		return this.femaleCount;
	}
	
	public int getNoBedCount() {
		final int[] total = { 0 };
		if (this.residents != null) {
			this.residents.stream()
					.forEach(h -> total[0] += h.hasBed() ? 0 : 1);
		}
		return total[0];
	}
	
	public Map<Integer, List<ResidentData>> getResidentHappinessStatistics() {
		int rangeIndex = 0;

		Map<Integer, List<ResidentData>> result = new LinkedHashMap<Integer, List<ResidentData>>();
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		
		if (this.residents != null) {
			this.residents.stream()
					.forEach(r -> {
						int key = result.keySet().stream()
							.filter(k -> r.getHappy() <= k)
							.findFirst().orElse(-1);
						if (key > -1) {
							result.get(key).add(r);
						}
					});
		}
		
		for (Entry<Integer, List<ResidentData>> entry : result.entrySet()) {
			if (entry != null) {
				entry.getValue().sort((c1, c2) -> {
					int compare = Integer.compare(c1.getHappy(), c2.getHappy());
					if (compare == 0) {
						compare = c1.getResidentName().compareTo(c2.getResidentName());
					}
					return compare;
				});
			}
		}
		
		return result;
	}
	
	public Map<Integer, List<ResidentData>> getResidentHungerStatistics() {
		int rangeIndex = 0;

		Map<Integer, List<ResidentData>> result = new LinkedHashMap<Integer, List<ResidentData>>();
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		result.put(STATISTICS_RANGE * rangeIndex++, new ArrayList<ResidentData>());
		
		if (this.residents != null) {
			this.residents.stream()
					.forEach(r -> {
						int key = result.keySet().stream()
							.filter(k -> r.getHunger() <= k)
							.findFirst().orElse(-1);
						if (key > -1) {
							result.get(key).add(r);
						}
					});
		}
		
		for (Entry<Integer, List<ResidentData>> entry : result.entrySet()) {
			if (entry != null) {
				entry.getValue().sort((c1, c2) -> {
					int compare = Integer.compare(c1.getHunger(), c2.getHunger());
					if (compare == 0) {
						compare = c1.getResidentName().compareTo(c2.getResidentName());
					}
					return compare;
				});
			}
		}
		
		return result;
	}
	
	private void clearData() {
		this.residentsCount = 0;
		
		this.residents = new ArrayList<ResidentData>();
		this.professionTypeCounts = new HashMap<ProfessionType, Integer>();

		this.adultCount = 0;
		this.childCount = 0;
		this.maleCount = 0;
		this.femaleCount = 0;
	}
	
	public void populateData(Village village) {
		clearData();
		
		if (village != null) {
			
			// get the resident data
			List<EntityVillagerTek> villageResidents = TektopiaUtils.getVillageResidents(village);
			
			this.residentsCount = village.getResidentCount();

			for (ProfessionType professionType : TektopiaUtils.getProfessionTypes()) {
				this.professionTypeCounts.put(professionType, 0);
			}
			
			for (EntityVillagerTek resident : villageResidents) {
				if (resident.isDead) {
					continue;
				}
				
				ProfessionType professionType = resident.getProfessionType();
				if (resident instanceof EntityGuard && ((EntityGuard)resident).isCaptain()) {
					professionType = ProfessionType.CAPTAIN;
				}
				
				this.professionTypeCounts.put(professionType, this.professionTypeCounts.get(professionType) + 1);
				this.residents.add(new ResidentData(resident));

				if (professionType == ProfessionType.CHILD)
					this.childCount++;
				else
					this.adultCount++;
				
				if (resident.isMale())
					this.maleCount++;
				else
					this.femaleCount++;
			}
			
			this.residentsCount = this.residents.size();
		}
	}
	
	public void readNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}
		
		clearData();
		
		if (nbtTag.hasKey(NBTTAG_VILLAGE_RESIDENTS)) {
			NBTTagCompound nbtResidentsData = nbtTag.getCompoundTag(NBTTAG_VILLAGE_RESIDENTS);
			
			this.residentsCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTSCOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTSCOUNT) : 0;

			for (ProfessionType professionType : TektopiaUtils.getProfessionTypes()) {
				String key = getProfessionTypeKey(professionType);
				this.professionTypeCounts.put(professionType, nbtResidentsData.hasKey(key) ? nbtResidentsData.getInteger(key) : 0);
			}
		
			for (int residentIndex = 0; residentIndex < this.residentsCount; residentIndex++) {
				String key = getResidentKey(residentIndex);
				
				if (nbtResidentsData.hasKey(key)) {
					ResidentData resident = new ResidentData();
					resident.readNBT(nbtResidentsData.getCompoundTag(key));
					this.residents.add(resident);
				}
			}
		
			this.adultCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTADULTCOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTADULTCOUNT) : 0;
			this.childCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT) : 0;
			this.maleCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTMALECOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTMALECOUNT) : 0;
			this.femaleCount = nbtResidentsData.hasKey(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT) ? nbtResidentsData.getInteger(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT) : 0;
		}
	}
	
	public void writeNBT(NBTTagCompound nbtTag) {
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
		}	
		
		NBTTagCompound nbtResidentsData = new NBTTagCompound();

		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTSCOUNT, this.residentsCount);

		if (this.professionTypeCounts != null) {
			for (Entry<ProfessionType, Integer> professionType : this.professionTypeCounts.entrySet()) {
				nbtResidentsData.setInteger(getProfessionTypeKey(professionType.getKey()), professionType.getValue());
			}
		}
		
		if (this.residents != null) {
			int residentIndex = 0;
			for (ResidentData resident : this.residents) {
				NBTTagCompound nbtResidentData = new NBTTagCompound();
				resident.writeNBT(nbtResidentData);
				nbtResidentsData.setTag(getResidentKey(residentIndex++), nbtResidentData);
			}
		}
		
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTADULTCOUNT, this.adultCount);
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTCHILDCOUNT, this.childCount);
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTMALECOUNT, this.maleCount);
		nbtResidentsData.setInteger(NBTTAG_VILLAGE_RESIDENTFEMALECOUNT, this.femaleCount);

		nbtTag.setTag(NBTTAG_VILLAGE_RESIDENTS, nbtResidentsData);
	}
	
	public static String getProfessionTypeKey(ProfessionType professionType) {
		return professionType.name();
	}

	public static String getResidentKey(int residentIndex) {
		return "resident@" + residentIndex;
	}
	
}
