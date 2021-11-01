package bletch.tektopiainformation.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.tektopiainformation.core.ModDetails;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.economy.ItemEconomy;
import net.tangotek.tektopia.economy.ItemValue;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureBarracks;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureType;

@ParametersAreNonnullByDefault
public class TektopiaUtils {

	public static final String PROFESSIONTYPE_ARCHITECT = "ARCHITECT";
	public static final String PROFESSIONTYPE_TRADESMAN = "TRADESMAN";
	
	public static List<Block> getTektopiaBlocks() {

		return StreamSupport.stream(Block.REGISTRY.spliterator(), false)
				.filter(b -> b.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA))
				.distinct()
				.sorted((b1, b2) -> b1.getClass().getTypeName().compareTo(b2.getClass().getTypeName()))
				.collect(Collectors.toList());
    }
	
	public static List<ItemStack> getTektopiaBlockStacks() {

		return StreamSupport.stream(Block.REGISTRY.spliterator(), false)
				.filter(b -> b.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA))
				.distinct()
				.map(b -> new ItemStack(b))
				.sorted((s1, s2) -> s1.getClass().getTypeName().compareTo(s2.getClass().getTypeName()))
				.collect(Collectors.toList());
    }
	
	public static List<Class<?>> getTektopiaBlockClasses() {

		return StreamSupport.stream(Block.REGISTRY.spliterator(), false)
				.filter(b -> b.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA))
				.map(b -> b.getClass())
				.filter(c -> !c.getTypeName().equalsIgnoreCase(Block.class.getTypeName()))
				.distinct()
				.sorted((c1, c2) -> c1.getTypeName().compareTo(c2.getTypeName()))
				.collect(Collectors.toList());
    }
	
	public static List<Item> getTektopiaItems() {

		return StreamSupport.stream(Item.REGISTRY.spliterator(), false)
        		.filter(i -> i.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA))
        		.distinct()
        		.sorted((i1, i2) -> i1.getClass().getTypeName().compareTo(i2.getClass().getTypeName()))
        		.collect(Collectors.toList());
    }
	
	public static List<ItemStack> getTektopiaItemStacks() {

		return StreamSupport.stream(Item.REGISTRY.spliterator(), false)
        		.filter(i -> i.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA))
        		.distinct()
        		.map(i -> new ItemStack(i))
        		.sorted((s1, s2) -> s1.getClass().getTypeName().compareTo(s2.getClass().getTypeName()))
        		.collect(Collectors.toList());
    }
	
	public static List<Class<?>> getTektopiaItemClasses() {

		return StreamSupport.stream(Item.REGISTRY.spliterator(), false)
        		.filter(i -> i.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID_TEKTOPIA))
        		.distinct()
        		.map(i -> i.getClass())
        		.sorted((c1, c2) -> c1.getTypeName().compareTo(c2.getTypeName()))
        		.collect(Collectors.toList());
    }   
	
	public static ProfessionType getProfessionType(String professionTypeName) {
    	if (professionTypeName == null || professionTypeName.trim().isEmpty()) {
    		return null;
    	}
    	
    	try {
        	return ProfessionType.valueOf(professionTypeName);
    	}
		catch (Exception ex) {
			return null;
		}
    }
	
	public static List<String> getProfessionTypeNames(Boolean includeAdditionals) {
		List<String> results = StreamSupport.stream(Arrays.spliterator(ProfessionType.values()), false)
			.distinct()
			.filter(p -> p != ProfessionType.NOMAD)
			.map(t -> t.name())
			.collect(Collectors.toList());
		
		if (includeAdditionals) {
			results.add(PROFESSIONTYPE_ARCHITECT);
			results.add(PROFESSIONTYPE_TRADESMAN);
		}
		
		return results.stream()
				.sorted((c1, c2) -> c1.compareTo(c2))
				.collect(Collectors.toList());
	}
	
	public static List<ProfessionType> getProfessionTypes() {
		return StreamSupport.stream(Arrays.spliterator(ProfessionType.values()), false)
			.distinct()
			.filter(p -> p != ProfessionType.NOMAD)
			.sorted((c1, c2) -> c1.name.compareTo(c2.name))
			.collect(Collectors.toList());
	}
	
	public static List<VillageStructureType> getVillageHomeTypes() {
		return StreamSupport.stream(Arrays.spliterator(VillageStructureType.values()), false)
				.distinct()
				.filter(t -> t.isHome() || t == VillageStructureType.BARRACKS)
				.sorted((c1, c2) -> c1.name().compareTo(c2.name()))
				.collect(Collectors.toList());
	}
	
	public static List<VillageStructureType> getVillageStructureTypes() {
		return StreamSupport.stream(Arrays.spliterator(VillageStructureType.values()), false)
				.distinct()
				.sorted((c1, c2) -> c1.name().compareTo(c2.name()))
				.collect(Collectors.toList());
	}
	
	public static Map<VillageStructureType, List<VillageStructure>> getVillageHomes(Village village) {
		if (village == null) {
			return new HashMap<VillageStructureType, List<VillageStructure>>(); 
		}
		
		HashMap<VillageStructureType, List<VillageStructure>> structuresList = new HashMap<VillageStructureType, List<VillageStructure>>(); 

		for (VillageStructureType structureType : getVillageHomeTypes()) {
			List<VillageStructure> structures = village.getStructures(structureType);
			if (structures == null) {
				structures = new ArrayList<VillageStructure>();
			}
			structuresList.put(structureType, structures);
		}
		
		return structuresList;
	}
	
	public static Map<VillageStructureType, List<VillageStructure>> getVillageStructures(Village village) {
		if (village == null) {
			return new HashMap<VillageStructureType, List<VillageStructure>>(); 
		}
		
		HashMap<VillageStructureType, List<VillageStructure>> structuresList = new HashMap<VillageStructureType, List<VillageStructure>>(); 

		for (VillageStructureType structureType : getVillageStructureTypes()) {
			List<VillageStructure> structures = village.getStructures(structureType);
			if (structures == null) {
				structures = new ArrayList<VillageStructure>();
			}
			structuresList.put(structureType, structures);
		}
		
		return structuresList;
	}
	
	public static List<EntityVillagerTek> getVillageResidents(Village village) {
		if (village == null) {
			return new ArrayList<EntityVillagerTek>();
		}
		
		try {
			Field field = Village.class.getDeclaredField("residents");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(village);
				if (fieldValue != null && fieldValue instanceof List<?>) {
					return ((List<?>)fieldValue).stream()
							.filter(v -> v instanceof EntityVillagerTek)
							.map(v -> (EntityVillagerTek)v)
							.sorted((c1, c2) -> (c1.getClass().getName() + "@" + c1.getLastName() + "@" + c1.getFirstName()).compareTo(c2.getClass().getName() + "@" + c2.getLastName() + "@" + c1.getFirstName()))
							.collect(Collectors.toList());
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return new ArrayList<EntityVillagerTek>();
	}
	
	public static int getStructureMaxBeds(VillageStructureHome homeStructure) {
		if (homeStructure == null) {
			return 0;
		}
		
		try {
			Field field = VillageStructureHome.class.getDeclaredField("maxBeds");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(homeStructure);
				if (fieldValue != null && fieldValue instanceof Integer) {
					return (int)fieldValue;
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return 0;
	}
	
	public static int getStructureMaxBeds(VillageStructureBarracks barracksStructure) {
		if (barracksStructure == null) {
			return 0;
		}
		
		try {
			Field field = VillageStructureHome.class.getDeclaredField("maxBeds");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(barracksStructure);
				if (fieldValue != null && fieldValue instanceof Integer) {
					return (int)fieldValue;
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return 0;
	}
	
	public static List<BlockPos> getStructureFloorTiles(VillageStructure structure) {
		if (structure == null) {
			return new ArrayList<BlockPos>();
		}
		
		try {
			Field field = VillageStructure.class.getDeclaredField("floorTiles");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(structure);
				if (fieldValue != null && fieldValue instanceof List<?>) {
					return ((List<?>)fieldValue).stream()
							.filter(v -> v instanceof BlockPos)
							.map(v -> (BlockPos)v)
							.collect(Collectors.toList());
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return new ArrayList<BlockPos>();
	}
	
	public static List<ItemStack> getEconomySalesHistory(ItemEconomy economy) {
		if (economy == null) {
			return null;
		}
		
		try {
			Field field = ItemEconomy.class.getDeclaredField("salesHistory");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(economy);
				if (fieldValue != null && fieldValue instanceof LinkedList<?>) {
					return ((LinkedList<?>)fieldValue).stream()
							.filter(v -> v instanceof ItemValue)
							.map(iv -> ((ItemValue)iv).getItemStack())
							.collect(Collectors.toList());
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return null;
	}
	
	public static int getVillagerSleepOffset(EntityVillagerTek villager) {
		if (villager == null) {
			return 0;
		}
		
		try {
			Field field = EntityVillagerTek.class.getDeclaredField("sleepOffset");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(villager);
				if (fieldValue != null && fieldValue instanceof Integer) {
					return (int)fieldValue;
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return 0;
	}
	
	public static List<Integer> getVillagerRecentEats(EntityVillagerTek villager) {
		if (villager == null) {
			return null;
		}
		
		try {
			Field field = EntityVillagerTek.class.getDeclaredField("recentEats");
			if (field != null) {
				field.setAccessible(true);
				
				Object fieldValue = field.get(villager);
				if (fieldValue != null && fieldValue instanceof LinkedList<?>) {
					return ((LinkedList<?>)fieldValue).stream()
							.filter(v -> v instanceof Integer)
							.map(iv -> (Integer)iv)
							.collect(Collectors.toList());
				}
			}
		}
		catch (Exception ex) {
			//do nothing if an error was encountered
		}
		
		return null;
	}

	public static long fixTime(long time) {
		while(time > 24000L) {
			time -= 24000L;
		}

		while(time < 0L) {
			time += 24000L;
		}

		return time;
	}

	public static int fixTime(int time) {
		while(time > 24000) {
			time -= 24000;
		}

		while(time < 0L) {
			time += 24000;
		}

		return time;
	}

	public static boolean isTimeOfDay(long timeOfDay, long startTime, long endTime) {
		startTime = fixTime(startTime);
		endTime = fixTime(endTime);
		
		if (endTime > startTime) {
			return timeOfDay >= startTime && timeOfDay <= endTime;
		} else {
			return timeOfDay >= startTime || timeOfDay <= endTime;
		}
	}
    
	public static String formatBlockPos(BlockPos blockPos) {
    	if (blockPos == null) {
    		return "";
    	}
    	
    	return blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ();
    }

	public static BlockPos getVillageSpawnPoint(World world, Village village) {
		int retries = 3;
		
		while (retries-- > 0) {
			BlockPos spawnPosition = village.getEdgeNode();

			if (isChunkFullyLoaded(world, spawnPosition)) {
				return spawnPosition;
			}
		}
		
		return null;
	}

	public static boolean isChunkFullyLoaded(World world, BlockPos pos) {
		if (world == null || world.isRemote || pos == null) {
			return true;
		}

		long i = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
		Chunk chunk = (Chunk)((ChunkProviderServer)world.getChunkProvider()).id2ChunkMap.get(i);
		return chunk != null && !chunk.unloadQueued;
	}

	public static Boolean trySpawnEntity(World world, BlockPos spawnPosition, Function<World, ?> createFunc) {
		if (world == null || spawnPosition == null || createFunc == null)
			return false;
		
		EntityLiving entity = (EntityLiving)createFunc.apply(world);
		if (entity == null)
			return false;
		
		entity.setLocationAndAngles((double)spawnPosition.getX() + 0.5D, (double)spawnPosition.getY(), (double)spawnPosition.getZ() + 0.5D, 0.0F, 0.0F);
		entity.onInitialSpawn(world.getDifficultyForLocation(spawnPosition), (IEntityLivingData)null);
		return world.spawnEntity(entity);
   }
	
}
