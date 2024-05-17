package com.gtnh.findit.util;

import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.service.blockfinder.BlockFoundResponse;

public class ClientFinderHelperUtils {

    public static void lookAtTarget(EntityClientPlayerMP player, BlockFoundResponse response) {
        rotateViewHelper(player, response.getPositions());
    }

    public static void rotateViewHelper(EntityClientPlayerMP player, List<ChunkPosition> targets) {
        if (targets.isEmpty()) {
            return;
        }

        Vec3 eyesPos = player.getPosition(1.0F);
        Vec3 nearestPos = center(getNearBlock(eyesPos, targets));

        Rotation targetRotation = Rotation.lookAt(eyesPos, nearestPos);
        targetRotation.apply(player);
    }

    public static Vec3 center(ChunkPosition p) {
        return Vec3.createVectorHelper(p.chunkPosX + 0.5, p.chunkPosY + 0.5, p.chunkPosZ + 0.5);
    }

    private static ChunkPosition getNearBlock(Vec3 player, List<ChunkPosition> targets) {
        ChunkPosition result = null;
        double minDistance = Double.MAX_VALUE;

        for (ChunkPosition target : targets) {
            double distance = getDistanceToCenter(player, target);
            if (distance < minDistance) {
                result = target;
                minDistance = distance;
            }
        }

        return result;
    }

    private static double getDistanceToCenter(Vec3 player, ChunkPosition target) {
        double deltaX = target.chunkPosX + 0.5 - player.xCoord;
        double deltaY = target.chunkPosY + 0.5 - player.yCoord;
        double deltaZ = target.chunkPosZ + 0.5 - player.zCoord;

        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ + deltaY * deltaY);
    }

    public static AxisAlignedBB mergeAABB(AxisAlignedBB box1, AxisAlignedBB box2) {
        return AxisAlignedBB.getBoundingBox(
                Math.min(box1.minX, box2.minX),
                Math.min(box1.minY, box2.minY),
                Math.min(box1.minZ, box2.minZ),
                Math.max(box1.maxX, box2.maxX),
                Math.max(box1.maxY, box2.maxY),
                Math.max(box1.maxZ, box2.maxZ));
    }
}
