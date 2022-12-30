package bletch.tektopiainformation.gui;

import bletch.common.utils.TextUtils;
import net.minecraft.util.ResourceLocation;

public class GuiButton {
	
	protected String key;
	protected GuiTexture icon;
	protected String buttonData;
	
	public GuiButton(String key) {
		this.key = key;
		this.icon = null;
		this.buttonData = "";
	}
	
	public GuiButton(String key, GuiTexture icon) {
		this(key);
		
		this.icon = icon;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public GuiTexture getIcon() {
		return this.icon;
	}
	
	public String getButtonData() {
		return this.buttonData;
	}
	
	public String getDisplayName() {
		return TextUtils.translate(getUnlocalizedName());
	}
	
	public String getUnlocalizedName() {
		return "button." + this.key + ".name";
	}
	
	public void setIcon(GuiTexture icon) {
		this.icon = icon;
	}
	
	public void setIcon(ResourceLocation texture, float zLevel, int left, int top, int width, int height, int textureLeft, int textureTop, int textureWidth, int textureHeight) {
		this.icon = new GuiTexture(texture, zLevel, left, top, width, height, textureLeft, textureTop, textureWidth, textureHeight);
	}
	
	public void setButtonData(String buttonData) {
		this.buttonData = buttonData;
	}
	
	public boolean withinBounds(int x, int y, float scale) {
		if (this.icon == null || this.icon.getTexture() == null) {
			return false;
		}
			
		return this.icon.withinBounds(x, y, scale);
	}
	
}
