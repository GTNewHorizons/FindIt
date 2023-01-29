package com.gtnh.findit.fx;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnh.findit.FindIt;

public class SlotHighlighter {

    private static final ResourceLocation highlightTexture = new ResourceLocation(
            FindIt.MOD_ID,
            "textures/gui/slot_highlight.png");

    public static void highlightSlots(GuiContainer gui, List<Integer> slots, int color) {
        if (slots.isEmpty()) {
            return;
        }

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        GL11.glPushMatrix();
        GL11.glTranslated(gui.guiLeft, gui.guiTop, 0);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Minecraft.getMinecraft().getTextureManager().bindTexture(highlightTexture);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(red, green, blue, alpha);

        for (int slotId : slots) {
            Slot slot = gui.inventorySlots.getSlot(slotId);
            highlightSlot(tessellator, slot.xDisplayPosition - 1, slot.yDisplayPosition - 1);
        }

        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glPopMatrix();
    }

    private static void highlightSlot(Tessellator tessellator, int x, int y) {
        double zLevel = 1;
        int x2 = x + 18;
        int y2 = y + 18;

        tessellator.addVertexWithUV(x2, y, zLevel, 18 / 32.0, 0);
        tessellator.addVertexWithUV(x, y, zLevel, 0, 0);
        tessellator.addVertexWithUV(x, y2, zLevel, 0, 18 / 32.0);
        tessellator.addVertexWithUV(x2, y2, zLevel, 18 / 32.0, 18 / 32.0);
    }
}
