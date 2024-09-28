package com.gtnh.findit.service.itemfinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.util.WorldUtils;

import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.conduit.TileConduitBundle;
import gregtech.api.metatileentity.BaseMetaPipeEntity;

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
                if (findItemInTile(player, tileEntity, request)) {
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

    private boolean findItemInTile(EntityPlayerMP player, TileEntity tileEntity, FindItemRequest request) {
        if (FindIt.isGregTechLoaded() && !FindItConfig.SEARCH_IN_GT_PIPES && tileEntity instanceof BaseMetaPipeEntity) {
            return false;
        }

        if (FindIt.isEnderIOLoaded() && !FindItConfig.SEARCH_IN_ENDERIO_CONDUITS
                && tileEntity instanceof TileConduitBundle) {
            return false;
        }

        for (IStackFilterProvider provider : FindIt.INSTANCE.pluginsList) {
            IStackFilter filter = provider.getFilter(player, tileEntity);
            if (filter != null && filter.matches(request)) {
                return true;
            }
        }

        return false;
    }
}
