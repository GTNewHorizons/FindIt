package com.gtnh.findit.util;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;


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

    public static AxisPair calculateAxisPair(Vec3 player, ChunkPosition target){
        double deltaX = target.chunkPosX - player.xCoord;
        double deltaY = target.chunkPosY - player.yCoord;
        double deltaZ = target.chunkPosZ - player.zCoord;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(Math.atan2(-deltaY, distanceXZ));

        return new AxisPair(yaw, pitch);
    }
}
