package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.IStackFilter.InventoryStackFilter;

import mrtjp.core.inventory.SimpleInventory;
import mrtjp.projectred.exploration.ItemBackpack;

public class ProjectRedExplorationProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        return null;
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemBackpack) {
            SimpleInventory inventory = new SimpleInventory(27, "", 64);
            inventory.loadInv(ItemBackpack.getBagTag(stack));

            return new InventoryStackFilter(player, inventory);
        }

        return null;
    }

}
