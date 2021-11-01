package bletch.tektopiainformation.commands;

import java.util.List;

import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.utils.LoggerUtils;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.entities.EntityNomad;

public class CommandNomadSpawn extends CommandMerchantBase {

	private static final String COMMAND_NAME = "spawn";
	
	public CommandNomadSpawn() {
		super(COMMAND_NAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			throw new WrongUsageException(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
		} 
		
		Boolean spawnNearMe = false;
		if (args.length > 0) {
			if (!args[0].equalsIgnoreCase("me")) {
				throw new WrongUsageException(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
			}
			
			spawnNearMe = true;
		}
		
		EntityPlayer entityPlayer = super.getCommandSenderAsPlayer(sender);
		World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;
		
		if (world == null || world.isRaining() || Village.isNightTime(world)) {
			notifyCommandListener(sender, this, NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".badconditions", new Object[0]);
			LoggerUtils.info(TextUtils.translate(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".badconditions", new Object[0]), true);
			return;
		}
		
		VillageManager villageManager = world != null ? VillageManager.get(world) : null;
		Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
		
		if (village == null) {
			notifyCommandListener(sender, this, NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage", new Object[0]);
			LoggerUtils.info(TextUtils.translate(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage", new Object[0]), true);
			return;
		}

		BlockPos spawnPosition = spawnNearMe ? entityPlayer.getPosition() : TektopiaUtils.getVillageSpawnPoint(world, village);
		
		if (spawnPosition == null) {
			notifyCommandListener(sender, this, NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".noposition", new Object[0]);
			LoggerUtils.info(TextUtils.translate(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".noposition", new Object[0]), true);
			return;
		}

        List<EntityNomad> entityList = world.getEntitiesWithinAABB(EntityNomad.class, village.getAABB().grow(Village.VILLAGE_SIZE));
        
        if (entityList.size() > 1) {
			notifyCommandListener(sender, this, NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".exists", new Object[0]);
			LoggerUtils.info(TextUtils.translate(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".exists", new Object[0]), true);
			return;
        }
        
		// attempt to spawn the nomad
		if (!TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityNomad(w))) {
			notifyCommandListener(sender, this, NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".failed", new Object[0]);
			LoggerUtils.info(TextUtils.translate(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".failed", new Object[0]), true);
			return;
		}
		
		notifyCommandListener(sender, this, NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) });
		LoggerUtils.info(TextUtils.translate(NomadCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) }), true);
	}
	
}
