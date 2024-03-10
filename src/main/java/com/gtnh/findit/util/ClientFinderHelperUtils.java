package com.gtnh.findit.util;

import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.service.blockfinder.BlockFoundResponse;

public class ClientFinderHelperUtils {

    public static void lookAtTarget(EntityClientPlayerMP player, BlockFoundResponse response) {
        rotateViewHelper(player, response.getPositions());
    }

    public static void rotateViewHelper(EntityClientPlayerMP player, List<ChunkPosition> targets) {
        Vec3 playerVec = player.getPosition(1.0F);
        Vec3 first = center(getNearBlock(playerVec, targets));

        AxisUtils.AxisPair pair = AxisUtils.calculateAxisPair(playerVec, first);

        player.rotationYaw = pair.yaw();
        player.rotationPitch = pair.pitch();
    }

    public static Vec3 center(ChunkPosition p) {
        return Vec3.createVectorHelper(p.chunkPosX + 0.5, p.chunkPosY + 0.5, p.chunkPosZ + 0.5);
    }

    private static ChunkPosition getNearBlock(Vec3 player, List<ChunkPosition> targets) {
        ChunkPosition result = null;
        double minDistance = Double.MAX_VALUE;

        for (ChunkPosition target : targets) {
            double distance = getDistance(player, target);
            if (distance < minDistance) {
                result = target;
                minDistance = distance;
            }
        }

        return result;
    }

    private static double getDistance(Vec3 player, ChunkPosition target) {
        double deltaX = target.chunkPosX - player.xCoord;
        double deltaY = target.chunkPosY - player.yCoord;
        double deltaZ = target.chunkPosZ - player.zCoord;

        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ + deltaY * deltaY);
    }
}
