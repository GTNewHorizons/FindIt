package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.brandon3055.draconicevolution.common.tileentities.TilePlacedItem;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;

public class DraconicEvolutionProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {

        if (tileEntity instanceof TilePlacedItem placedItem) {
            final ItemStack stack = placedItem.getStack();
            return request -> request.isStackSatisfies(player, stack);
        }

        return null;
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        return null;
    }

}
