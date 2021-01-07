package bletch.tektopiainformation.network.handlers;

import bletch.tektopiainformation.core.ModConfig;
import bletch.tektopiainformation.gui.GuiTektopiaBook;
import bletch.tektopiainformation.network.messages.VillageMessageToClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class VillageMessageHandlerOnClient implements IMessageHandler<VillageMessageToClient, IMessage> {

	/**
	 * Called when a message is received of the appropriate type.
	 * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
	 * @param message The message
	 */
	@Override
	public IMessage onMessage(VillageMessageToClient message, MessageContext ctx) {
		
		if (ctx.side != Side.CLIENT) {
			System.err.println("VillageMessageToClient received on wrong side: " + ctx.side);
			return null;
		}

	    // we know for sure that this handler is only used on the client side, so it is ok to assume that the ctx handler is a client, and that Minecraft exists.
	    // Packets received on the server side must be handled differently!
		
		// This code creates a new task which will be executed by the client during the next tick
		// In this case, the task is to call messageHandlerOnClient.processMessage(minecraft, message)
	    Minecraft minecraft = Minecraft.getMinecraft();
	    minecraft.addScheduledTask(new Runnable()
	    {
	    	public void run() {
	    		processMessage(minecraft, message);
	    	}
	    });
	    
		return null;
	}

	// This message is called from the Client thread.
	void processMessage(Minecraft minecraft, VillageMessageToClient message) {
		// check if the message is valid
		if (message == null) {
			System.err.println("VillageMessageToClient was null");
			return;
		}
		if (!message.isMessageValid()) {
			System.err.println("VillageMessageToClient was invalid - " + message.toString());
			return;
		}
		
		// open the GUI screen for the book
		if (ModConfig.gui.enableGuiIntegration && ModConfig.gui.tektopiaInformationBook.enableTektopiaInformationBook) {
			minecraft.displayGuiScreen(new GuiTektopiaBook(message.getVillageData()));
		}
	}
}
