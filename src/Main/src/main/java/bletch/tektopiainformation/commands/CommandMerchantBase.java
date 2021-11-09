package bletch.tektopiainformation.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class CommandMerchantBase extends CommandBase {
	
    protected String name;
    
    public CommandMerchantBase(String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public List<String> getAliases() {
        return Collections.singletonList(this.name);
    }

    @Nonnull
    public String getUsage(@Nullable ICommandSender sender) {
        return MerchantCommands.COMMAND_PREFIX + this.name + ".usage";
    }
    
    public boolean checkPermission(@Nullable MinecraftServer server, @Nullable ICommandSender sender) {
        try {
            if (sender == null) {
                return false;
            }
            return PermissionAPI.hasPermission(getCommandSenderAsPlayer(sender), MerchantCommands.COMMAND_PREFIX_WITH_MODID + this.getName());
        }
        catch (PlayerNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Nonnull
    public List<String> getTabCompletions(@Nullable MinecraftServer server, @Nullable ICommandSender sender, @Nullable String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

}
