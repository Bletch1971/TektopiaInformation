package bletch.tektopiainformation.gui;

import java.util.ArrayList;
import java.util.List;

import bletch.tektopiainformation.enums.GuiMapMarkerType;
import bletch.tektopiainformation.enums.GuiMapQuadrant;
import net.minecraft.util.math.BlockPos;

public class GuiMapMarker extends GuiButton implements Comparable<GuiMapMarker> {

	private GuiMapMarkerType markerType;
	private BlockPos position;
	private GuiTooltip tooltip;
	private List<GuiMapQuadrant> quadrants;
	private int priority;
	
	public GuiMapMarker(String key) {
		super(key);

		this.markerType = GuiMapMarkerType.UNKNOWN;
		this.position = null;
		this.tooltip = null;
		this.priority = 1;
		setQuadrants();
	}
	
	public GuiMapMarker(String key, GuiMapMarkerType markerType, GuiTexture icon, BlockPos position, GuiTooltip tooltip) {
		super(key, icon);
		
		this.markerType = markerType;
		this.position = position;
		this.tooltip = tooltip;
		this.priority = 1;
		setQuadrants();
	}
	
	public GuiMapMarker addPosition(BlockPos position) {
		this.position = this.position.add(position);
		
		return this;
	}

	public GuiMapMarkerType getMarkerType() {
		return this.markerType;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public GuiTooltip getTooltip() {
		return this.tooltip;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public Boolean isInQuadrant(GuiMapQuadrant quadrant) {
		return this.quadrants.contains(quadrant);
	}
	
	public GuiMapMarker setPosition(int left, int top) {
		this.position = new BlockPos(left, this.position.getY(), top);
		
		return this;
	}
	
	public GuiMapMarker setPosition(BlockPos position) {
		this.position = position;
		
		return this;
	}
	
	public GuiMapMarker setPriority(int priority) {
		this.priority = priority;
		
		return this;
	}

	private void setQuadrants() {
		this.quadrants = new ArrayList<GuiMapQuadrant>();
		
		if (this.position == null)
			return;
		
		int x = this.position.getX();
		int z = this.position.getZ();
		
		this.quadrants.add(GuiMapQuadrant.ALL);
		if (x >= 0 && z >= 0)
			this.quadrants.add(GuiMapQuadrant.SOUTHEAST);
		if (x >= 0 && z <= 0)
			this.quadrants.add(GuiMapQuadrant.NORTHEAST);
		if (x <= 0 && z >= 0)
			this.quadrants.add(GuiMapQuadrant.SOUTHWEST);
		if (x <= 0 && z <= 0)
			this.quadrants.add(GuiMapQuadrant.NORTHWEST);
	}
 
    // Override the compareTo method
    public int compareTo(GuiMapMarker marker)
    {
    	int result = Integer.compare(this.priority, marker.getPriority());
    	if (result != 0)
    		return result;
    	
		if (this.position.getZ() == marker.position.getZ()) {
        	if (this.position.getX() == marker.position.getX())
        		return 0;
        	
    		return this.position.getX() > marker.position.getX() ? 1 : -1;
    	}
    	
    	return this.position.getZ() > marker.position.getZ() ? 1 : -1;
    }
	
	public boolean withinBounds(int x, int y, float scale) {
		if (this.tooltip == null) {
			return super.withinBounds(x, y, scale);
		}
			
		return this.tooltip.withinBounds(x, y, scale);
	}
}
