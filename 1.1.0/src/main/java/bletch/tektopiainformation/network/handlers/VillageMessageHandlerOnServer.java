package bletch.tektopiainformation.network.handlers;

import bletch.tektopiainformation.network.messages.VillageMessageToClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class VillageMessageHandlerOnServer implements IMessageHandler<VillageMessageToClient, IMessage> {

	@Override
	public IMessage onMessage(VillageMessageToClient message, MessageContext ctx) {
	    System.err.println("VillageMessageToClient received on wrong side: " + ctx.side);
	    return null;
	}

}
