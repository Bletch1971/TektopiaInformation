package bletch.tektopiainformation.network.messages;

import java.io.IOException;

import bletch.tektopiainformation.network.data.VillageData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class VillageMessageToClient implements IMessage {

	private boolean messageIsValid;
	private VillageData villageData;
	
	// for use by the message handler only.
	public VillageMessageToClient() {
		this.messageIsValid = false;
		this.villageData = null;
	}
	
	public VillageMessageToClient(VillageData villageData) {
		this.messageIsValid = villageData != null;
		this.villageData = villageData;
	}	
	
	public boolean isMessageValid() {
		return this.messageIsValid;
	}
	
	public VillageData getVillageData() {
		return this.villageData;
	}
	
	/**
	 * Called by the network code once it has received the message bytes over the network.
	 * Used to read the ByteBuf contents into your member variables
	 * @param buffer
	 */
	@Override
	public void fromBytes(ByteBuf buffer) {
		try {
			readBuffer(new PacketBuffer(buffer));
		} catch (IOException e) {
			System.err.println("VillageMessageToClient.fromBytes threw an IOException: " + e.getLocalizedMessage());
		}		
	}

	/**
	 * Called by the network code.
	 * Used to write the contents of your message member variables into the ByteBuf, ready for transmission over the network.
	 * @param buffer
	 */
	@Override
	public void toBytes(ByteBuf buffer) {
		try {
			writeBuffer(new PacketBuffer(buffer));
		} catch (IOException e) {
			System.err.println("VillageMessageToClient.toBytes threw an IOException: " + e.getLocalizedMessage());
		}
	}
	
	private void readBuffer(PacketBuffer buffer) throws IOException {
		this.villageData = new VillageData();
		this.villageData.readBuffer(buffer);

		this.messageIsValid = true;
	}
	
	private void writeBuffer(PacketBuffer buffer) throws IOException {
		if (!this.messageIsValid) return;
		
		if (this.villageData != null) {
			this.villageData.writeBuffer(buffer);
		}
	}
	
}
