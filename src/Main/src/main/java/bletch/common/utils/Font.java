package bletch.common.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
public class Font {
	
    public final static Font small = new Font(true);
    public final static Font normal = new Font(false);

    public FontRenderer fontRenderer;

    private Font(boolean small) {
        Minecraft mc = Minecraft.getMinecraft();
        fontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), small);
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(fontRenderer);
    }

    public int getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    public void printLeft(Object o, int x, int y, float z) {
    	printLeft(o, x, y, z, Color.BLACK.getRGB(), false);
    }

    public void printLeft(Object o, int x, int y, float z, int color) {
    	printLeft(o, x, y, z, color, false);
    }

    public void printLeft(Object o, int x, int y, float z, int color, boolean shadow) {
    	GlStateManager.pushMatrix();
    	GlStateManager.translate((float)1, (float)1, z + 1.0F);
        fontRenderer.drawString(String.valueOf(o), x, y, color, shadow);
        GlStateManager.popMatrix();
    }
    
    public void printCentered(Object o, int x, int y, float z) {
    	printCentered(o, x, y, z, Color.BLACK.getRGB(), false);
    }
    
    public void printCentered(Object o, int x, int y, float z, int color) {
    	printCentered(o, x, y, z, color, false);
    }
    
    public void printCentered(Object o, int x, int y, float z, int color, boolean shadow) {
    	int width = getStringWidth(String.valueOf(o));
    	int newX = x - (width / 2);
    	
    	GlStateManager.pushMatrix();
    	GlStateManager.translate((float)1, (float)1, z + 1.0F);
    	fontRenderer.drawString(String.valueOf(o), newX, y, color, shadow);
        GlStateManager.popMatrix();
    }
    
    public void printRight(Object o, int x, int y, float z) {
    	printRight(o, x, y, z, Color.BLACK.getRGB(), false);
    }
    
    public void printRight(Object o, int x, int y, float z, int color) {
    	printRight(o, x, y, z, color, false);
    }
    
    public void printRight(Object o, int x, int y, float z, int color, boolean shadow) {
    	int width = getStringWidth(String.valueOf(o));
    	int newX = x - width;
    	
    	GlStateManager.pushMatrix();
    	GlStateManager.translate((float)1, (float)1, z + 1.0F);
    	fontRenderer.drawString(String.valueOf(o), newX, y, color, shadow);
        GlStateManager.popMatrix();
    }
    
    public String trimStringToWidth(String text, int width, boolean addElipse) {
    	if (fontRenderer.getStringWidth(text) > width) {
    		return fontRenderer.trimStringToWidth(text, width) + (addElipse ? "..." : "");
    	} else {
    		return text;
    	}
    }
    
    public List<String> splitStringToWidth(String text, int width) {
    	List<String> lines = new ArrayList<>();
    	
    	if (!StringUtils.isNullOrWhitespace(text)) {
        	int textWidth = fontRenderer.getStringWidth(text);
        	
        	while (textWidth > width) {
        		String textPartial = fontRenderer.trimStringToWidth(text, width);
        		if (textPartial.contains(" ")) {
        			while (!textPartial.endsWith(" ")) {
        				textPartial = textPartial.substring(0, textPartial.length() - 1);
        			}
        		}
        		lines.add(textPartial.trim());
        		
        		text = text.replaceFirst(textPartial, "");
        		textWidth = fontRenderer.getStringWidth(text);
        	}
        	
        	if (!StringUtils.isNullOrWhitespace(text)) {
        		lines.add(text.trim());
        	}
    	}
    	
    	return lines;
    }
    
}
