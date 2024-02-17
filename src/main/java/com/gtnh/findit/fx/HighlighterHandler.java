package com.gtnh.findit.fx;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

// inspired by McJtyLib

public class HighlighterHandler {

    private static List<ChunkPosition> positions = new ArrayList<>();
    private static long expireHighlight;

    public static void highlightBlocks(List<ChunkPosition> positions, long expireHighlight) {
        HighlighterHandler.positions = positions;
        HighlighterHandler.expireHighlight = expireHighlight;
    }

    public static void renderHilightedBlock(RenderWorldLastEvent event) {
        List<ChunkPosition> list = HighlighterHandler.positions;

        if (list.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        long time = System.currentTimeMillis();

        EntityPlayerSP p = mc.thePlayer;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks;

        if (((time / 500) & 1) == 0) {
            return;
        }

        if (time > expireHighlight) {
            positions.clear();
        }

        for (ChunkPosition c : list) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glLineWidth(3);
            GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            renderHighLightedBlocksOutline(c.chunkPosX, c.chunkPosY, c.chunkPosZ);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    static void renderHighLightedBlocksOutline(double x, double y, double z) {
        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y + 1, z);
        GL11.glVertex3d(x, y + 1, z + 1);
        GL11.glVertex3d(x, y, z + 1);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(x + 1, y, z);
        GL11.glVertex3d(x + 1, y + 1, z);
        GL11.glVertex3d(x + 1, y + 1, z + 1);
        GL11.glVertex3d(x + 1, y, z + 1);
        GL11.glVertex3d(x + 1, y, z);

        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x + 1, y, z);
        GL11.glVertex3d(x + 1, y, z + 1);
        GL11.glVertex3d(x, y, z + 1);
        GL11.glVertex3d(x, y + 1, z + 1);
        GL11.glVertex3d(x + 1, y + 1, z + 1);
        GL11.glVertex3d(x + 1, y + 1, z);
        GL11.glVertex3d(x + 1, y, z);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x + 1, y, z);
        GL11.glVertex3d(x + 1, y + 1, z);
        GL11.glVertex3d(x, y + 1, z);
        GL11.glVertex3d(x, y + 1, z + 1);
        GL11.glVertex3d(x + 1, y + 1, z + 1);
        GL11.glVertex3d(x + 1, y, z + 1);
        GL11.glVertex3d(x, y, z + 1);

        GL11.glEnd();
    }
}
