package bletch.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
	
	@SideOnly(Side.CLIENT)
    public static void renderEntity(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();

        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 0F, false);
        rendermanager.setRenderShadow(true);
        
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        
        RenderHelper.disableStandardItemLighting();
        
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
	
	@SideOnly(Side.CLIENT)
	public static void renderItemIntoGUI(ItemStack stack, int x, int y) {
		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
	}
	
	@SideOnly(Side.CLIENT)
	public static void renderItemAndEffectIntoGUI(ItemStack stack, int x, int y) {
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
	}
	
	@SideOnly(Side.CLIENT)
    /**
     * Renders the stack size and/or damage bar for the given ItemStack.
     */
	public static void renderItemOverlayIntoGUI(FontRenderer fontRenderer, ItemStack stack, int x, int y) {
    	GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getRenderItem().renderItemOverlays(fontRenderer, stack, x, y);
		GlStateManager.disableLighting();
        GlStateManager.popMatrix();
	}
    
}
