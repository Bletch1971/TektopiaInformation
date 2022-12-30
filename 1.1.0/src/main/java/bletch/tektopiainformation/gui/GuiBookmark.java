package bletch.tektopiainformation.gui;

import net.minecraft.util.ResourceLocation;

public class GuiBookmark extends GuiButton {
	
	private final int pageIndex;
	protected GuiTexture background;
	
	public GuiBookmark(String key, int pageIndex) {
		super(key);
		
		this.pageIndex = pageIndex;
		this.background = null;
	}
	
	public GuiBookmark(String key, int pageIndex, GuiTexture background) {
		super(key);
		
		this.pageIndex = pageIndex;
		this.background = background;
	}
	
	public GuiBookmark(String key, int pageIndex, GuiTexture background, GuiTexture icon) {
		super(key, icon);
		
		this.pageIndex = pageIndex;
		this.background = background;
	}
	
	public int getPageIndex() {
		return this.pageIndex;
	}
	
	public GuiTexture getBackground() {
		return this.background;
	}
	public void setBackground(GuiTexture texture) {
		this.background = texture;
	}
	
	public void setBackground(ResourceLocation texture, float zLevel, int left, int top, int width, int height, int textureLeft, int textureTop, int textureWidth, int textureHeight) {
		this.background = new GuiTexture(texture, zLevel, left, top, width, height, textureLeft, textureTop, textureWidth, textureHeight);
	}
	
	public boolean withinBounds(int x, int y, float scale) {
		if (this.background == null || this.background.getTexture() == null) {
			return false;
		}
			
		return this.background.withinBounds(x, y, scale);
	}
	
	@Override
	public String getUnlocalizedName() {
		return "bookmark." + this.key + ".name";
	}
	
}
