package bletch.tektopiainformation.gui;

import java.util.ArrayList;
import java.util.List;

import bletch.tektopiainformation.enums.GuiMapMarkerType;
import bletch.tektopiainformation.enums.GuiMapQuadrant;
import net.minecraft.util.math.BlockPos;

public class GuiMapMarker extends GuiButton implements Comparable<GuiMapMarker> {

	private GuiMapMarkerType markerType;
	private BlockPos offset;
	private GuiTooltip tooltip;
	private List<GuiMapQuadrant> quadrants;
	
	public GuiMapMarker(String key) {
		super(key);

		this.markerType = GuiMapMarkerType.UNKNOWN;
		this.offset = null;
		this.tooltip = null;
		setQuadrants();
	}
	
	public GuiMapMarker(String key, GuiMapMarkerType markerType, GuiTexture icon, BlockPos offset, GuiTooltip tooltip) {
		super(key, icon);
		
		this.markerType = markerType;
		this.offset = offset;
		this.tooltip = tooltip;
		setQuadrants();
	}

	public GuiMapMarkerType getMarkerType() {
		return this.markerType;
	}

	public BlockPos getOffset() {
		return this.offset;
	}

	public GuiTooltip getTooltip() {
		return this.tooltip;
	}
	
	public Boolean isInQuadrant(GuiMapQuadrant quadrant) {
		return this.quadrants.contains(quadrant);
	}

	private void setQuadrants() {
		this.quadrants = new ArrayList<GuiMapQuadrant>();
		
		if (this.offset == null)
			return;
		
		int x = this.offset.getX();
		int z = this.offset.getZ();
		
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
    	//if (this.markerType.ordinal() == marker.markerType.ordinal()) {
    		if (this.offset.getZ() == marker.offset.getZ()) {
            	if (this.offset.getX() == marker.offset.getX())
            		return 0;
            	
        		return this.offset.getX() > marker.offset.getX() ? 1 : -1;
        	}
        	
        	return this.offset.getZ() > marker.offset.getZ() ? 1 : -1;
    	//}
    	
    	//return this.markerType.ordinal() > marker.markerType.ordinal() ? 1 : -1;
    }
}
