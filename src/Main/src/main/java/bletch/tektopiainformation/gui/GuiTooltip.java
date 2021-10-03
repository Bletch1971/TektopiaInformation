package bletch.tektopiainformation.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class GuiTooltip {
	
	private int left = 0;
	private int top = 0;
	private int leftOffset = 0;
	private int topOffset = 0;
	private int width = 0;
	private int height = 0;
	private List<String> tooltip;
	
	public GuiTooltip(int left, int top, int width, int height, List<String> tooltip) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.tooltip = tooltip;
	}
	
	public GuiTooltip(int left, int top, int leftOffset, int topOffset, int width, int height, List<String> tooltip) {
		this.left = left;
		this.top = top;
		this.leftOffset = leftOffset;
		this.topOffset = topOffset;
		this.width = width;
		this.height = height;
		this.tooltip = tooltip;
	}
	
	public GuiTooltip(int left, int top, int width, int height, String tooltip) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.tooltip = Arrays.asList(tooltip);
	}
	
	public GuiTooltip(int left, int top, int leftOffset, int topOffset, int width, int height, String tooltip) {
		this.left = left;
		this.top = top;
		this.leftOffset = leftOffset;
		this.topOffset = topOffset;
		this.width = width;
		this.height = height;
		this.tooltip = Arrays.asList(tooltip);
	}
	
	public GuiTooltip addPosition(BlockPos position) {
		this.left += position.getX();
		this.top += position.getZ();
		
		return this;
	}
	
	public int getLeft() {
		return this.left + this.leftOffset;
	}
	
	public int getTop() {
		return this.top + this.topOffset;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getRight() {
		return getLeft() + this.width;
	}
	
	public int getBottom() {
		return getTop() + this.height;
	}
	
	public List<String> getTooltip() {
		return this.tooltip;
	}
	
	public GuiTooltip multiplyPosition(int factor) {
		this.left *= factor;
		this.top *= factor;
		
		return this;
	}
	
	public GuiTooltip multipleOffset(int factor) {
		this.leftOffset *= factor;
		this.topOffset *= factor;
		
		return this;
	}
	
	public GuiTooltip multiplySize(int factor) {
		this.width *= factor;
		this.height *= factor;
		
		return this;
	}
	
	public GuiTooltip setPosition(int left, int top) {
		this.left = left;
		this.top = top;
		
		return this;
	}
	
	public GuiTooltip setPosition(BlockPos position) {
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
