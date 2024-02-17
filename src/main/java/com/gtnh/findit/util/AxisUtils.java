package com.gtnh.findit.util;

import net.minecraft.util.Vec3;

public class AxisUtils {

    public static final class AxisPair {

        private final float yaw;
        private final float pitch;

        public AxisPair(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float yaw() {
            return yaw;
        }

        public float pitch() {
            return pitch;
        }
    }

    public static AxisPair calculateAxisPair(Vec3 player, Vec3 target) {
        double deltaX = target.xCoord - player.xCoord;
        double deltaY = target.yCoord - player.yCoord;
        double deltaZ = target.zCoord - player.zCoord;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(Math.atan2(-deltaY, distanceXZ));

        return new AxisPair(yaw, pitch);
    }
}
