package bletch.tektopiainformation.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class CommandNomadBase extends CommandBase {
	
    protected String name;
    
    public CommandNomadBase(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getAliases() {
        return Collections.singletonList(this.name);
    }
    
    public String getUsage(ICommandSender sender) {
        return NomadCommands.COMMAND_PREFIX + this.name + ".usage";
    }
    
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        try {
            return PermissionAPI.hasPermission(getCommandSenderAsPlayer(sender), NomadCommands.COMMAND_PREFIX_WITH_MODID + this.getName());
        }
        catch (PlayerNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getRequiredPermissionLevel() {
        return 4;
    }
    
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

}
