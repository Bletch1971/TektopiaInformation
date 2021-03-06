package bletch.tektopiainformation.network.handlers;

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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVillagerTek;

/*
 * http://maven.thiakil.com/forge-1.12-javadoc/net/minecraftforge/event/entity/player/PlayerInteractEvent.html
 */
public class PlayerInteractHandler {
	
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
				if (entity instanceof EntityArchitect || entity instanceof EntityTradesman ||
						entity instanceof EntityVillagerTek && ((EntityVillagerTek)entity).isRole(VillagerRole.VILLAGER)) {
					
					// check if the player is holding a minecraft book
					ItemStack itemInHand = event.getEntityPlayer().getHeldItem(event.getHand());
					if (itemInHand != null && itemInHand != ItemStack.EMPTY && itemInHand.getItem() == Item.getByNameOrId("minecraft:book")) {
						
						EntityVillagerTek villager = (EntityVillagerTek)event.getTarget();				
						Village village = villager.getVillage();
						
						if (village != null && village.isLoaded()) {
							if (event.isCancelable()) {
						        // cancel event, open GUI
								event.setCancellationResult(EnumActionResult.SUCCESS);
								event.setResult(Result.ALLOW);
								event.setCanceled(true);
					        }
					        
							// create the village data
							VillageData villageData = new VillageData(village);
							villageData.setResident(villager);
							
							// create the message to be sent to the client
							VillageMessageToClient message = new VillageMessageToClient(villageData);
							
							// send the message containing the village data to the client player
							TektopiaInformation.NETWORK.sendTo(message, (EntityPlayerMP)event.getEntityPlayer());
				        }
			        }
		        }
				
				if (entity instanceof EntityItemFrame) {
					
					// check if the player is holding a minecraft book
					ItemStack itemInHand = event.getEntityPlayer().getHeldItem(event.getHand());
					if (itemInHand != null && itemInHand != ItemStack.EMPTY && itemInHand.getItem() == Item.getByNameOrId("minecraft:book")) {
						
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
					        
							// create the village data
							VillageData villageData = new VillageData(village);
							villageData.setFramePosition(event.getPos());
							
							// create the message to be sent to the client
							VillageMessageToClient message = new VillageMessageToClient(villageData);
							
							// send the message containing the village data to the client player
							TektopiaInformation.NETWORK.sendTo(message, (EntityPlayerMP)event.getEntityPlayer());
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
				if (block != null && block instanceof BlockBed) {
					
					// check if the player is holding a minecraft book
					ItemStack itemInHand = event.getEntityPlayer().getHeldItem(event.getHand());
					if (itemInHand != null && itemInHand != ItemStack.EMPTY && itemInHand.getItem() == Item.getByNameOrId("minecraft:book")) {
						
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
					        
							// create the village data
							VillageData villageData = new VillageData(village);
							villageData.setBedPosition(event.getPos());
							
							// create the message to be sent to the client
							VillageMessageToClient message = new VillageMessageToClient(villageData);
							
							// send the message containing the village data to the client player
							TektopiaInformation.NETWORK.sendTo(message, (EntityPlayerMP)event.getEntityPlayer());
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
	
}
