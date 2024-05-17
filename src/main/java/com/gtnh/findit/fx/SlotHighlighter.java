package com.gtnh.findit.fx;

import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnh.findit.FindIt;

import codechicken.nei.guihook.IContainerDrawHandler;

public class SlotHighlighter implements IContainerDrawHandler {

    private static final ResourceLocation highlightTexture = new ResourceLocation(
            FindIt.MOD_ID,
            "textures/gui/slot_highlight.png");

    private GuiContainer gui = null;
    private HashSet<Slot> slots = new HashSet<>();
    private float red = 0;
    private float green = 0;
    private float blue = 0;
    private float alpha = 0;

    public void highlightSlots(GuiContainer gui, HashSet<Slot> slots, int color) {

        this.gui = gui;
        this.slots = slots;

        this.red = (float) (color >> 16 & 255) / 255.0F;
        this.green = (float) (color >> 8 & 255) / 255.0F;
        this.blue = (float) (color & 255) / 255.0F;
        this.alpha = (float) (color >> 24 & 255) / 255.0F;
    }

    @Override
    public void onPreDraw(GuiContainer gui) {}

    @Override
    public void renderObjects(GuiContainer gui, int mousex, int mousey) {}

    @Override
    public void postRenderObjects(GuiContainer gui, int mousex, int mousey) {}

    @Override
    public void renderSlotOverlay(GuiContainer gui, Slot slot) {}

    @Override
    public void renderSlotUnderlay(GuiContainer gui, Slot slot) {
        if (this.gui == gui && this.slots.contains(slot)) {
            GL11.glPushMatrix();

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            Minecraft.getMinecraft().getTextureManager().bindTexture(highlightTexture);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            highlightSlot(tessellator, slot.xDisplayPosition, slot.yDisplayPosition);
            tessellator.draw();

            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);

            GL11.glPopMatrix();
        }
    }

    private void highlightSlot(Tessellator tessellator, int x, int y) {
        double zLevel = 1;
        int x2 = x + 16;
        int y2 = y + 16;

        tessellator.addVertexWithUV(x2, y, zLevel, 18 / 32.0, 0);
        tessellator.addVertexWithUV(x, y, zLevel, 0, 0);
        tessellator.addVertexWithUV(x, y2, zLevel, 0, 18 / 32.0);
        tessellator.addVertexWithUV(x2, y2, zLevel, 18 / 32.0, 18 / 32.0);
    }
}
