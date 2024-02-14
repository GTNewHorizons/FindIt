package com.gtnh.findit.util;

import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.service.itemfinder.ItemFoundResponse;

public class ClientFinderHelperUtils {

    public static void lookAtTarget(EntityClientPlayerMP player, ItemFoundResponse response) {
        rotateViewHelper(player, response.getPositions());
    }

    public static void lookAtTarget(EntityClientPlayerMP player, BlockFoundResponse response) {
        rotateViewHelper(player, response.getPositions());
    }

    public static void rotateViewHelper(EntityClientPlayerMP player, List<ChunkPosition> targets) {
        Vec3 playerVec = player.getPosition(1.0F);
        ChunkPosition first = targets.remove(0);

        AxisUtils.AxisPair pair = AxisUtils.calculateAxisPair(playerVec, first);

        player.rotationYaw = pair.yaw();
        player.rotationPitch = pair.pitch();
    }
}
