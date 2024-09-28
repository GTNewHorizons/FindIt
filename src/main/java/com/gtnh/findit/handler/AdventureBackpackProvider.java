package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.inventory.IInventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.AnyMultiItemFilter;
import com.gtnh.findit.IStackFilter.FluidStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.IStackFilter.InventoryStackFilter;

public class AdventureBackpackProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {

        if (tileEntity instanceof IInventoryBackpack inventory) {
            return getFilter(player, inventory);
        }

        return null;
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemAdventureBackpack) {
            return getFilter(player, new InventoryBackpack(stack));
        }

        return null;
    }

    private IStackFilter getFilter(EntityPlayer player, IInventoryBackpack inventory) {
        final AnyMultiItemFilter filter = new AnyMultiItemFilter();
        final FluidStackFilter fluidFilter = new FluidStackFilter();

        filter.add(new InventoryStackFilter(player, inventory));

        for (FluidTank tank : inventory.getTanksArray()) {
            fluidFilter.add(tank.getFluid());
        }

        if (!fluidFilter.isEmpty()) {
            filter.add(fluidFilter);
        }

        return filter;
    }

}
