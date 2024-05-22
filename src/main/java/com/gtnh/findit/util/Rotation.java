package com.gtnh.findit.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class Rotation {

    private final float yaw;
    private final float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public void apply(Entity entity) {
        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;
    }

    public static Rotation lookAt(Vec3 source, Vec3 target) {
        double deltaX = target.xCoord - source.xCoord;
        double deltaY = target.yCoord - source.yCoord;
        double deltaZ = target.zCoord - source.zCoord;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(Math.atan2(-deltaY, distanceXZ));

        return new Rotation(yaw, pitch);
    }
}
