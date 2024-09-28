package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.IStackFilter.InventoryStackFilter;

import de.eydamos.backpack.item.ItemBackpack;

public class BackpackProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        return null;
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemBackpack) {
            return new InventoryStackFilter(player, ItemBackpack.getInventory(stack, player));
        }

        return null;
    }

}
