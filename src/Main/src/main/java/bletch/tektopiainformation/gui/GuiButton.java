package bletch.tektopiainformation.gui;

import bletch.common.utils.TextUtils;
import net.minecraft.util.ResourceLocation;

public class GuiButton {
	
	protected String key;
	protected GuiTexture icon;
	
	public GuiButton(String key) {
		this.key = key;
		this.icon = null;
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
	
	public String getDisplayName() {
		return TextUtils.translate(getUnlocalizedName());
	}
	
	public String getUnlocalizedName() {
		return "button." + this.key + ".name";
	}
	
	public void setIcon(GuiTexture icon) {
		this.icon = icon;
	}
	
	public void setIcon(ResourceLocation texture, int left, int top, int width, int height, int textureLeft, int textureTop, int textureWidth, int textureHeight) {
		this.icon = new GuiTexture(texture, left, top, width, height, textureLeft, textureTop, textureWidth, textureHeight);
	}
	
	public boolean withinBounds(int x, int y, float scale) {
		if (this.icon == null || this.icon.getTexture() == null) {
			return false;
		}
			
		return this.icon.withinBounds(x, y, scale);
	}
	
}
