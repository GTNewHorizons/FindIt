package com.gtnh.findit.service.itemfinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import com.brandon3055.draconicevolution.common.tileentities.TilePlacedItem;
import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
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
                if (findItemInTile(tileEntity, request)) {
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

    private boolean findItemInTile(TileEntity tileEntity, FindItemRequest request) {
        if (FindIt.isGregTechLoaded()) {
            if (!FindItConfig.SEARCH_IN_GT_PIPES && tileEntity instanceof BaseMetaPipeEntity) {
                return false;
            }
        }
        if (FindIt.isEnderIOLoaded()) {
            if (!FindItConfig.SEARCH_IN_ENDERIO_CONDUITS && tileEntity instanceof TileConduitBundle) {
                return false;
            }
        }

        if (FindIt.isDraconicEvolutionLoaded()) {
            if (tileEntity instanceof TilePlacedItem placedItem && request.isStackSatisfies(placedItem.getStack())) {
                return true;
            }
        }

        if (request.hasFluidStack()) {
            if (tileEntity instanceof IFluidTank && request.isFluidSatisfies(((IFluidTank) tileEntity).getFluid())) {
                return true;
            }

            if (tileEntity instanceof IFluidHandler) {
                FluidTankInfo[] tankInfo = ((IFluidHandler) tileEntity).getTankInfo(ForgeDirection.UNKNOWN);

                for (FluidTankInfo info : tankInfo) {
                    if (request.isFluidSatisfies(info.fluid)) {
                        return true;
                    }
                }
            }
        }

        if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                if (request.isStackSatisfies(inventory.getStackInSlot(slot))) {
                    return true;
                }
            }
        }

        return false;
    }
}
