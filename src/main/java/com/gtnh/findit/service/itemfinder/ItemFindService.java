package com.gtnh.findit.service.itemfinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.util.WorldUtils;

import cpw.mods.fml.relauncher.Side;

public class ItemFindService {

    public ItemFindService() {
        FindItNetwork.registerMessage(FindItemRequest.Handler.class, FindItemRequest.class, Side.SERVER);
        FindItNetwork.registerMessage(ItemFoundResponse.Handler.class, ItemFoundResponse.class, Side.CLIENT);
    }

    public void handleRequest(EntityPlayerMP player, FindItemRequest request) {
        if (FindIt.getCooldownService().checkSearchCooldown(player)) {
            return;
        }

        List<ChunkPosition> positions = new ArrayList<>();

        for (TileEntity tileEntity : WorldUtils.getTileEntitiesAround(player, FindItConfig.SEARCH_RADIUS)) {
            try {
                if (request.isTileSatisfies(player, tileEntity)) {
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
        FindItNetwork.CHANNEL.sendTo(new ItemFoundResponse(request.getStackToFind()), player);
    }
}
