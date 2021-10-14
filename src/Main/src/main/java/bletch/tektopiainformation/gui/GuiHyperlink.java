package bletch.tektopiainformation.gui;

import bletch.tektopiainformation.core.ModDetails;
import net.minecraft.util.ResourceLocation;

public class GuiHyperlink extends GuiButton {
	
	public static final ResourceLocation BUTTON_INVISIBLE = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_blank.png");

	private String linkData;
	
	public GuiHyperlink(String key, String linkData) {
		super(key);
		
		this.linkData = linkData;
	}

	public String getLinkData() {
		return this.linkData;
	}
	
	public void setIcon(int left, int top, int width, int height) {
		this.icon = new GuiTexture(BUTTON_INVISIBLE, left, top, width, height, 0, 0, width, height);
	}

}
