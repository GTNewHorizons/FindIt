package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.IStackFilter.InventoryStackFilter;

import forestry.storage.inventory.ItemInventoryBackpack;
import forestry.storage.items.ItemBackpack;

public class ForestryStackFilterProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        return null;
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemBackpack backpack) {
            final IInventory inventory = new ItemInventoryBackpack(player, backpack.getBackpackSize(), stack);

            return new InventoryStackFilter(player, inventory);
        }

        return null;
    }

}
