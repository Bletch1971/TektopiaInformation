package bletch.tektopiainformation.commands;

import java.util.List;
import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.utils.LoggerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.entities.EntityMerchant;

public class CommandMerchantKill extends CommandMerchantBase {

	private static final String COMMAND_NAME = "kill";
	
	public CommandMerchantKill() {
		super(COMMAND_NAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			throw new WrongUsageException(MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage");
		} 
		
		EntityPlayer entityPlayer = super.getCommandSenderAsPlayer(sender);
		World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;
		
		VillageManager villageManager = world != null ? VillageManager.get(world) : null;
		Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
		if (village == null) {
			notifyCommandListener(sender, this, MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage");
			LoggerUtils.info(TextUtils.translate(MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage"), true);
			return;
		}

        List<EntityMerchant> entityList = world.getEntitiesWithinAABB(EntityMerchant.class, village.getAABB().grow(Village.VILLAGE_SIZE));
        if (entityList.size() == 0) {
			notifyCommandListener(sender, this, MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists");
			LoggerUtils.info(TextUtils.translate(MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists"), true);
			return;
        }
        
        for (EntityMerchant entity : entityList) {
        	if (entity.isDead)
        		continue;
        	
        	entity.setDead();
        	
        	String name = (entity.isMale() ? TextFormatting.BLUE : TextFormatting.LIGHT_PURPLE) + entity.getName();
    		
    		notifyCommandListener(sender, this, MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", name);
    		LoggerUtils.info(TextUtils.translate(MerchantCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", name), true);
        }
	}

}
