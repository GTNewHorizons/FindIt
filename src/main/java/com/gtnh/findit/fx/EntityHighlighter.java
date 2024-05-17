package com.gtnh.findit.fx;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

public class EntityHighlighter {

    private List<Integer> entityIds = new ArrayList<>();
    private long expireHighlight;

    public void highlightEntities(List<Integer> entityIds, long expireHighlight) {
        this.entityIds = entityIds;
        this.expireHighlight = expireHighlight;
    }

    public void renderHighlightedEntities(RenderWorldLastEvent event) {
        if (this.entityIds.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        RenderItem renderItem = RenderItem.getInstance();
        long time = System.currentTimeMillis();

        if (((time / 500) & 1) == 0) {
            return;
        }

        if (time > expireHighlight) {
            entityIds.clear();
        }

        EntityPlayerSP p = mc.thePlayer;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glLineWidth(3);
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(0.75f, 0.0f, 1.0f, 1.0f);

        for (int entityId : this.entityIds) {
            Entity entity = mc.theWorld.getEntityByID(entityId);
            if (entity != null) {

                float scale = 1f;

                if (entity instanceof EntityItem) {
                    EntityItem item = (EntityItem) entity;
                    GL11.glPushMatrix();

                    double itemX = item.lastTickPosX + (item.posX - item.lastTickPosX) * event.partialTicks;
                    double itemY = item.lastTickPosY + (item.posY - item.lastTickPosY) * event.partialTicks;
                    double itemZ = item.lastTickPosZ + (item.posZ - item.lastTickPosZ) * event.partialTicks;
                    float age = item.age + event.partialTicks;

                    float elevation = renderItem.shouldBob()
                            ? MathHelper.sin(age / 10.0F + item.hoverStart) * 0.1F + 0.1F
                            : 0F;
                    float angle = (age / 20.0F + item.hoverStart) * (180F / (float) Math.PI);

                    GL11.glTranslated(itemX, itemY, itemZ);
                    GL11.glRotated(angle, 0, 1, 0);
                    GL11.glTranslated(-itemX, -itemY + elevation, -itemZ);
                    scale = 1.2f;
                }

                FxHelper.renderEntityOutline(entity, scale, event.partialTicks);

                if (entity instanceof EntityItem) {
                    GL11.glPopMatrix();
                }
            }
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
