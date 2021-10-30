package bletch.tektopiainformation.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiTexture {
	
	private ResourceLocation texture;
	private float zLevel = 0;
	
	private int left = 0;
	private int top = 0;
	private int width = 0;
	private int height = 0;
	
	private int textureLeft = 0;
	private int textureTop = 0;
	private int textureWidth = 0;
	private int textureHeight = 0;
	
	public GuiTexture(ResourceLocation texture, float zLevel, int left, int top, int width, int height, int textureLeft, int textureTop, int textureWidth, int textureHeight) {
		this.texture = texture;
		this.zLevel = zLevel;
		
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;

		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}
	
	public GuiTexture addPosition(BlockPos position) {
		this.left += position.getX();
		this.top += position.getZ();
		
		return this;
	}
	
	public ResourceLocation getTexture() {
		return this.texture;
	}
	
	public float getZLevel() {
		return this.zLevel;
	}
	
	public int getLeft() {
		return this.left;
	}
	
	public int getTop() {
		return this.top;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getRight() {
		return this.left + this.width;
	}
	
	public int getBottom() {
		return this.top + this.height;
	}
	
	public int getTextureLeft() {
		return this.textureLeft;
	}
	
	public int getTextureTop() {
		return this.textureTop;
	}
	
	public int getTextureWidth() {
		return this.textureWidth;
	}
	
	public int getTextureHeight() {
		return this.textureHeight;
	}
	
	public int getTextureRight() {
		return this.textureLeft + this.textureWidth;
	}
	
	public int getTextureBottom() {
		return this.textureTop + this.textureHeight;
	}
	
	public GuiTexture multiplyPosition(int factor) {
		this.left *= factor;
		this.top *= factor;
		
		return this;
	}
	
	public GuiTexture multiplySize(int factor) {
		this.width *= factor;
		this.height *= factor;
		this.textureWidth *= factor;
		this.textureHeight *= factor;
		
		return this;
	}
	
	public GuiTexture setPosition(int left, int top) {
		this.left = left;
		this.top = top;
		
		return this;
	}
	
	public GuiTexture setPosition(BlockPos position) {
		this.left = position.getX();
		this.top = position.getZ();
		
		return this;
	}
	
	public boolean withinBounds(int x, int y, float scale) {
		int scaledX = (int) (x / scale);
		int scaledY = (int) (y / scale);
		
		if (getLeft() <= scaledX && getTop() <= scaledY && getRight() >= scaledX && getBottom() >= scaledY) {
			return true;
		}
		
		return false;
	}
	
}
