package com.gtnh.findit.fx;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;

import org.lwjgl.opengl.GL11;

public class FxHelper {

    public static void renderBlockOutline(ChunkPosition pos) {
        renderOutline(
                pos.chunkPosX,
                pos.chunkPosY,
                pos.chunkPosZ,
                pos.chunkPosX + 1,
                pos.chunkPosY + 1,
                pos.chunkPosZ + 1);
    }

    public static void renderBoxOutline(AxisAlignedBB box) {
        renderOutline(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static void renderEntityOutline(Entity entity, float scale, float partialTicks) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
        float w = entity.width / 2.0F;
        float h = entity.height;

        if (scale != 1f) {
            w *= scale;
            h *= scale;
            y -= entity.height * (scale - 1f) / 2f;
        }

        renderOutline(x - (double) w, y, z - (double) w, x + (double) w, y + (double) h, z + (double) w);
    }

    public static void renderOutline(double x0, double y0, double z0, double x1, double y1, double z1) {
        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex3d(x0, y0, z0);
        GL11.glVertex3d(x0, y1, z0);
        GL11.glVertex3d(x0, y1, z1);
        GL11.glVertex3d(x0, y0, z1);
        GL11.glVertex3d(x0, y0, z0);

        GL11.glVertex3d(x1, y0, z0);
        GL11.glVertex3d(x1, y1, z0);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y0, z1);
        GL11.glVertex3d(x1, y0, z0);

        GL11.glVertex3d(x0, y0, z0);
        GL11.glVertex3d(x1, y0, z0);
        GL11.glVertex3d(x1, y0, z1);
        GL11.glVertex3d(x0, y0, z1);
        GL11.glVertex3d(x0, y1, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y1, z0);
        GL11.glVertex3d(x1, y0, z0);
        GL11.glVertex3d(x0, y0, z0);
        GL11.glVertex3d(x1, y0, z0);
        GL11.glVertex3d(x1, y1, z0);
        GL11.glVertex3d(x0, y1, z0);
        GL11.glVertex3d(x0, y1, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y0, z1);
        GL11.glVertex3d(x0, y0, z1);

        GL11.glEnd();
    }
}
