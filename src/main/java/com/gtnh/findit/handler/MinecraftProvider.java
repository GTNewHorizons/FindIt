package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.AnyMultiItemFilter;
import com.gtnh.findit.IStackFilter.FluidStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.IStackFilter.InventoryStackFilter;

import codechicken.nei.recipe.StackInfo;

public class MinecraftProvider implements IStackFilterProvider {

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        AnyMultiItemFilter anyFilter = new AnyMultiItemFilter();

        if (tileEntity instanceof IInventory inventory) {
            anyFilter.add(new InventoryStackFilter(player, inventory));
        }

        if (tileEntity instanceof IFluidTank tank) {
            anyFilter.add(new FluidStackFilter(tank.getFluid()));
        }

        if (tileEntity instanceof IFluidHandler handler) {
            FluidTankInfo[] tankInfo = handler.getTankInfo(ForgeDirection.UNKNOWN);
            FluidStackFilter filter = new FluidStackFilter();
            if (tankInfo != null) {
                for (FluidTankInfo info : tankInfo) {
                    filter.add(info.fluid);
                }
            }
            if (!filter.isEmpty()) anyFilter.add(filter);
        }

        return anyFilter.isEmpty() ? null : anyFilter;
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        AnyMultiItemFilter filter = new AnyMultiItemFilter();

        final FluidStack fluid = StackInfo.getFluid(stack);

        if (fluid != null) {
            filter.add(new FluidStackFilter(fluid));
        }

        filter.add(request -> StackInfo.equalItemAndNBT(request.getStackToFind(), stack, true));

        return filter.isEmpty() ? null : filter;
    }

}
