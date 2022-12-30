package bletch.tektopiainformation.network.handlers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import bletch.tektopiainformation.TektopiaInformation;
import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.network.data.VillageData;
import bletch.tektopiainformation.network.messages.VillageMessageToClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.entities.EntityVillageNavigator;

/*
 * http://maven.thiakil.com/forge-1.12-javadoc/net/minecraftforge/event/entity/player/PlayerInteractEvent.html
 */
public class PlayerInteractHandler {
	
	private static Map<UUID, Long> playerTimeouts = new HashMap<UUID, Long>();
	
	/*
	 * This event is fired on both sides when the player right clicks an entity.
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getSide() == Side.CLIENT) {
			// do not process on client side
			return;
		}
			
		if (event.getSide() == Side.SERVER) {
			
			// check if the information book is enabled
			if (ModConfig.gui.enableGuiIntegration && ModConfig.gui.tektopiaInformationBook.enableTektopiaInformationBook) {
				
				// check if the entity is valid
				Entity entity = event.getTarget();
				if (entity instanceof EntityVillageNavigator) {
					
					// check if the player is holding a minecraft book
					ItemStack itemInHand = event.getEntityPlayer().getHeldItem(event.getHand());
					if (!itemInHand.isEmpty() && itemInHand.getItem() == Item.getByNameOrId("minecraft:book")) {
						
						EntityVillageNavigator villageEntity = (EntityVillageNavigator)event.getTarget();				
						Village village = villageEntity.getVillage();
						
						if (village != null && village.isLoaded()) {
							if (event.isCancelable()) {
						        // cancel event, open GUI
								event.setCancellationResult(EnumActionResult.SUCCESS);
								event.setResult(Result.ALLOW);
								event.setCanceled(true);
					        }
					        
							EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
					        
							SendMessage(player, village, null, null, villageEntity);
				        }
			        }
		        }
				
				if (entity instanceof EntityItemFrame) {
					
					// check if the player is holding a minecraft book
					ItemStack itemInHand = event.getEntityPlayer().getHeldItem(event.getHand());
					if (!itemInHand.isEmpty() && itemInHand.getItem() == Item.getByNameOrId("minecraft:book")) {
						
						Village village = null;
						
						VillageManager villageManager = VillageManager.get(event.getWorld());
						if (villageManager != null) {
							village = villageManager.getVillageAt(event.getPos());
						}
						
						if (village != null) {							
							if (event.isCancelable()) {
						        // cancel event, open GUI
								event.setCancellationResult(EnumActionResult.SUCCESS);
								event.setResult(Result.ALLOW);
								event.setCanceled(true);
					        }
					        
							EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
					        
							SendMessage(player, village, event.getPos(), null, null);
				        }
			        }
				}
			}
		}
	}
	
	/*
	 * This event is fired on both sides when the player right clicks an entity.
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		
	}
	
	/*
	 * This event is fired when a player left clicks while targeting a block.
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
		
	}
	
	/*
	 * This event is fired on both sides whenever the player right clicks while targeting a block.
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if (event.getSide() == Side.CLIENT) {
			// do not process on client side
			return;
		}
			
		if (event.getSide() == Side.SERVER) {
			
			// check if the information book is enabled
			if (ModConfig.gui.enableGuiIntegration && ModConfig.gui.tektopiaInformationBook.enableTektopiaInformationBook) {
				
				// check if the block is valid
				Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
				if (block instanceof BlockBed) {
					
					// check if the player is holding a minecraft book
					ItemStack itemInHand = event.getEntityPlayer().getHeldItem(event.getHand());
					if (!itemInHand.isEmpty() && itemInHand.getItem() == Item.getByNameOrId("minecraft:book")) {
						
						Village village = null;
						
						VillageManager villageManager = VillageManager.get(event.getWorld());
						if (villageManager != null) {
							village = villageManager.getVillageAt(event.getPos());
						}
						
						if (village != null) {							
							if (event.isCancelable()) {
						        // cancel event, open GUI
								event.setCancellationResult(EnumActionResult.SUCCESS);
								event.setResult(Result.ALLOW);
								event.setCanceled(true);
					        }
					        
							EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
					        
							SendMessage(player, village, null, event.getPos(), null);
				        }
			        }
		        }
			}
		}
	}
	
	/*
	 * This event is fired on the client side when the player right clicks empty space with an empty hand.
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event) {
		
	}
	
	/*
	 * This event is fired on both sides before the player triggers Item.onItemRightClick(net.minecraft.world.World, net.minecraft.entity.player.EntityPlayer, net.minecraft.util.EnumHand).
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {

	}
	
	private void SendMessage(EntityPlayerMP player, Village village, BlockPos framePosition, BlockPos bedPosition, EntityVillageNavigator entity) {	
		
		long previousSeconds = playerTimeouts.getOrDefault(player.getUniqueID(), 0L);
		long currentSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
									
		if (previousSeconds + ModConfig.gui.tektopiaInformationBook.bookGracePeriod > currentSeconds) {
			return;
		}
	    
		// create the village data
		VillageData villageData = new VillageData(village, player.getPosition());
		
		if (framePosition != null) {
			villageData.setFramePosition(framePosition);
		}
		if (bedPosition != null) {
			villageData.setBedPosition(bedPosition);
		}
		if (entity != null) {
			villageData.setEntity(entity);
		}
		
		// create the message to be sent to the client
		VillageMessageToClient message = new VillageMessageToClient(villageData);
		
		// send the message containing the village data to the client player
		TektopiaInformation.NETWORK.sendTo(message, player);
		
		playerTimeouts.put(player.getUniqueID(), currentSeconds);
		
	}
	
}