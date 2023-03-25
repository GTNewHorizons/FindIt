package com.gtnh.findit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldUtils {

    public static List<TileEntity> getTileEntitiesAround(Entity entity, int r) {
        int x = (int) Math.floor(entity.posX);
        int y = (int) entity.posY;
        int z = (int) Math.floor(entity.posZ);
        return getTileEntitiesInRegion(entity.worldObj, x - r, y - r, z - r, x + r + 1, y + r + 1, z + r + 1);
    }

    /**
     * @param (x1,y1,z1) included
     * @param (x2,y2,z2) excluded
     */
    public static List<TileEntity> getTileEntitiesInRegion(World world, int x1, int y1, int z1, int x2, int y2,
            int z2) {
        List<TileEntity> result = new ArrayList<>();
        int cx1 = x2 >> 4;
        int cz1 = z2 >> 4;

        for (int cx = x1 >> 4; cx <= cx1; cx++) {
            for (int cz = z1 >> 4; cz <= cz1; cz++) {
                Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
                if (chunk != null) {
                    @SuppressWarnings("unchecked")
                    Map<ChunkPosition, TileEntity> tiles = chunk.chunkTileEntityMap;
                    for (TileEntity tile : tiles.values()) {
                        if (!tile.isInvalid() && tile.xCoord >= x1
                                && tile.xCoord < x2
                                && tile.yCoord >= y1
                                && tile.yCoord < y2
                                && tile.zCoord >= z1
                                && tile.zCoord < z2) {
                            result.add(tile);
                        }
                    }
                }
            }
        }

        return result;
    }
}
