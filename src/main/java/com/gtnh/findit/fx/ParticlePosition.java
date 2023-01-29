package com.gtnh.findit.fx;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class ParticlePosition extends EntityReddustFX {

    private static int validVersion = 0;

    private int version;

    public ParticlePosition(World world, ChunkPosition pos) {
        this(
                world,
                pos.chunkPosX + (0.5 + (world.rand.nextDouble() - 0.5) * world.rand.nextDouble()),
                pos.chunkPosY + (0.5 + (world.rand.nextDouble() - 0.5) * world.rand.nextDouble()),
                pos.chunkPosZ + (0.5 + (world.rand.nextDouble() - 0.5) * world.rand.nextDouble()));
        version = validVersion;
    }

    @Override
    public void onUpdate() {
        if (version == validVersion) {
            super.onUpdate();
        } else {
            setDead();
        }
    }

    public ParticlePosition(World world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ, 255 / 255f, 165 / 255f, 255 / 255f);
        this.noClip = true;
        this.particleMaxAge *= 10;
        this.motionX *= 0.1;
        this.motionY *= 0.1;
        this.motionZ *= 0.1;
        this.particleScale *= 2.0f;
    }

    @Override
    public void renderParticle(final Tessellator tessellator, float partialTickTime, float rotationX, float rotationZ,
            float rotationYZ, float rotationXY, float rotationXZ) {
        tessellator.draw();

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        tessellator.startDrawingQuads();
        super.renderParticle(tessellator, partialTickTime, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        tessellator.startDrawingQuads();
    }

    public static void highlightBlocks(World world, List<ChunkPosition> positions) {
        validVersion++;
        int particleCount = positions.size() <= 10 ? 12 : Math.max(12 - (positions.size() - 10) / 2, 2);
        for (ChunkPosition pos : positions) {
            for (int i = 0; i < particleCount; ++i) {
                Minecraft.getMinecraft().effectRenderer.addEffect(new ParticlePosition(world, pos));
            }
        }
    }
}
