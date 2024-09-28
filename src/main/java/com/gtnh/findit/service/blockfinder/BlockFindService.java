package com.gtnh.findit.service.blockfinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.util.WorldUtils;

import cpw.mods.fml.relauncher.Side;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class BlockFindService {

    public BlockFindService() {
        FindItNetwork.registerMessage(FindBlockRequest.Handler.class, FindBlockRequest.class, Side.SERVER);
        FindItNetwork.registerMessage(BlockFoundResponse.Handler.class, BlockFoundResponse.class, Side.CLIENT);
    }

    public void handleRequest(EntityPlayerMP player, FindBlockRequest request) {
        if (FindIt.getCooldownService().checkSearchCooldown(player)) {
            return;
        }

        List<ChunkPosition> positions = new ArrayList<>();
        for (TileEntity tileEntity : WorldUtils.getTileEntitiesAround(player, FindItConfig.SEARCH_RADIUS)) {
            try {
                Block tileBlock = tileEntity.getBlockType();

                if (!request.getBlockToFind().equals(tileBlock)) {
                    continue;
                }

                int tileMeta;
                if (FindIt.isGregTechLoaded() && tileEntity instanceof IGregTechTileEntity gregTech) {
                    tileMeta = gregTech.getMetaTileID();
                } else {
                    tileMeta = tileEntity.getBlockMetadata();
                }

                if (request.getMetaToFind() == tileMeta) {
                    positions.add(new ChunkPosition(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
                    if (positions.size() == FindItConfig.MAX_RESPONSE_SIZE) {
                        break;
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        FindItNetwork.CHANNEL.sendTo(new BlockFoundResponse(positions), player);
    }
}
