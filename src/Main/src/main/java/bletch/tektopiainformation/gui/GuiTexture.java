package bletch.tektopiainformation.gui;

import net.minecraft.util.ResourceLocation;

public class GuiTexture {
	
	private ResourceLocation texture;
	
	private int left = 0;
	private int top = 0;
	private int width = 0;
	private int height = 0;
	
	private int textureLeft = 0;
	private int textureTop = 0;
	private int textureWidth = 0;
	private int textureHeight = 0;
	
	private float scale;
	
	public GuiTexture(ResourceLocation texture, int left, int top, int width, int height, int textureLeft, int textureTop, int textureWidth, int textureHeight, float scale) {
		this.texture = texture;
		
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;

		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		
		this.scale = scale;
	}
	
	public ResourceLocation getTexture() {
		return this.texture;
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
	
	public float getScale() {
		return this.scale;
	}
	
	public boolean withinBounds(int x, int y) {
		int scaledX = (int) (x / this.scale);
		int scaledY = (int) (y / this.scale);
		
		if (getLeft() <= scaledX && getTop() <= scaledY && getRight() >= scaledX && getBottom() >= scaledY) {
			return true;
		}
		
		return false;
	}
	
}
