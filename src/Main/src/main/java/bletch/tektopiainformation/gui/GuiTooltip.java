package bletch.tektopiainformation.gui;

import java.util.Arrays;
import java.util.List;

public class GuiTooltip {
	
	private int left = 0;
	private int top = 0;
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
	
	public GuiTooltip(int left, int top, int width, int height, String tooltip) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.tooltip = Arrays.asList(tooltip);
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
	
	public List<String> getTooltip() {
		return this.tooltip;
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
