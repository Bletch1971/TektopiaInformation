package bletch.tektopiainformation.commands;

import java.util.List;

import bletch.common.commands.CommonCommandBase;
import bletch.common.utils.TektopiaUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModDetails;
import bletch.tektopiainformation.utils.LoggerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.entities.EntityMerchant;

public class CommandMerchantSpawn extends CommonCommandBase {

	private static final String COMMAND_NAME = "spawn";
	
	public CommandMerchantSpawn() {
		super(ModDetails.MOD_ID, MerchantCommands.COMMAND_PREFIX, COMMAND_NAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			throw new WrongUsageException(this.prefix + COMMAND_NAME + ".usage");
		} 
		
		boolean spawnNearMe = false;
		if (args.length > 0) {
			if (!args[0].equalsIgnoreCase("me")) {
				throw new WrongUsageException(this.prefix + COMMAND_NAME + ".usage");
			}
			
			spawnNearMe = true;
		}
		
		EntityPlayer entityPlayer = super.getCommandSenderAsPlayer(sender);
		World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;
		
		if (world == null || world.isRaining() || Village.isNightTime(world)) {
			notifyCommandListener(sender, this, this.prefix + COMMAND_NAME + ".badconditions");
			LoggerUtils.instance.info(TextUtils.translate(this.prefix + COMMAND_NAME + ".badconditions"), true);
			return;
		}
		
		VillageManager villageManager = world != null ? VillageManager.get(world) : null;
		Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
		
		if (village == null) {
			notifyCommandListener(sender, this, this.prefix + COMMAND_NAME + ".novillage");
			LoggerUtils.instance.info(TextUtils.translate(this.prefix + COMMAND_NAME + ".novillage"), true);
			return;
		}

		BlockPos spawnPosition = spawnNearMe ? entityPlayer.getPosition() : TektopiaUtils.getVillageSpawnPoint(world, village);
		
		if (spawnPosition == null) {
			notifyCommandListener(sender, this, this.prefix + COMMAND_NAME + ".noposition");
			LoggerUtils.instance.info(TextUtils.translate(this.prefix + COMMAND_NAME + ".noposition"), true);
			return;
		}

        List<EntityMerchant> entityList = world.getEntitiesWithinAABB(EntityMerchant.class, village.getAABB().grow(Village.VILLAGE_SIZE));
        
        if (entityList.size() > 0) {
			notifyCommandListener(sender, this, this.prefix + COMMAND_NAME + ".exists");
			LoggerUtils.instance.info(TextUtils.translate(this.prefix + COMMAND_NAME + ".exists"), true);
			return;
        }
        
		// attempt to spawn the merchant
		if (!TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityMerchant(w))) {
			notifyCommandListener(sender, this, this.prefix + COMMAND_NAME + ".failed");
			LoggerUtils.instance.info(TextUtils.translate(this.prefix + COMMAND_NAME + ".failed"), true);
			return;
		}
		
		notifyCommandListener(sender, this, this.prefix + COMMAND_NAME + ".success", TektopiaUtils.formatBlockPos(spawnPosition));
		LoggerUtils.instance.info(TextUtils.translate(this.prefix + COMMAND_NAME + ".success", TektopiaUtils.formatBlockPos(spawnPosition)), true);
	}

}