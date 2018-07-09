package com.gtnh.findit.proxy.client;

import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticlePosition extends EntityReddustFX {

    public ParticlePosition(World world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ, 1.0f, 1.0f, 1.0f);
        this.noClip = true;
        this.particleMaxAge *= 10;
        this.motionX *= 0.1;
        this.motionY *= 0.1;
        this.motionZ *= 0.1;
        this.particleScale *= 2.0f;
    }

    @Override
    public void renderParticle(final Tessellator tessellator, float partialTickTime, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(tessellator, partialTickTime, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        tessellator.draw();

        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

        tessellator.startDrawingQuads();
        super.renderParticle(tessellator, partialTickTime, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        tessellator.draw();

        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        tessellator.startDrawingQuads();

    }
}
