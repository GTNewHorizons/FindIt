package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;

import gregtech.api.covers.CoverRegistry;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.util.GTUtility;

public class GregTechCoverProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        if (!(tileEntity instanceof ICoverable coverable)) {
            return null;
        }

        return request -> {
            ItemStack stackToFind = request.getStackToFind();
            if (stackToFind == null || !CoverRegistry.isCover(stackToFind)) {
                return false;
            }

            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                if (!coverable.hasCoverAtSide(side)) {
                    continue;
                }

                ItemStack coverStack = coverable.getCoverItemAtSide(side);
                if (GTUtility.areStacksEqual(stackToFind, coverStack, true)) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        return null;
    }
}
