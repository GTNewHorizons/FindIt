package com.gtnh.findit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.findit.service.itemfinder.FindItemRequest;

@FunctionalInterface
public interface IStackFilter {

    boolean matches(FindItemRequest request);

    public static interface IStackFilterProvider {

        IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity);

        IStackFilter getFilter(EntityPlayer player, ItemStack stack);

    }

    public static class InventoryStackFilter implements IStackFilter {

        private final IInventory inventory;
        private final EntityPlayer player;

        public InventoryStackFilter(EntityPlayer player, IInventory inventory) {
            this.inventory = inventory;
            this.player = player;
        }

        @Override
        public boolean matches(FindItemRequest request) {

            for (int slot = 0; slot < this.inventory.getSizeInventory(); slot++) {
                ItemStack slotItem = this.inventory.getStackInSlot(slot);
                if (slotItem != null && request.isStackSatisfies(this.player, slotItem)) {
                    return true;
                }
            }

            return false;
        }

    }

    public static class FluidStackFilter implements IStackFilter {

        private final List<FluidStack> fluids;

        public FluidStackFilter(List<FluidStack> fluids) {
            this.fluids = fluids;
        }

        public FluidStackFilter() {
            this(new ArrayList<>());
        }

        public FluidStackFilter(FluidStack fluid) {
            this();
            add(fluid);
        }

        public void add(FluidStack fluid) {
            if (fluid != null) {
                this.fluids.add(fluid);
            }
        }

        public boolean isEmpty() {
            return this.fluids.isEmpty();
        }

        @Override
        public boolean matches(FindItemRequest request) {

            for (FluidStack fluid : this.fluids) {
                if (fluid != null && request.isFluidSatisfies(fluid)) {
                    return true;
                }
            }

            return false;
        }

    }

    public static class AnyMultiItemFilter implements IStackFilter {

        private final List<IStackFilter> filters;

        public AnyMultiItemFilter(List<IStackFilter> filters) {
            this.filters = filters;
        }

        public AnyMultiItemFilter() {
            this(new ArrayList<>());
        }

        public void add(IStackFilter filter) {
            if (filter != null) {
                this.filters.add(filter);
            }
        }

        public boolean isEmpty() {
            return this.filters.isEmpty();
        }

        @Override
        public boolean matches(FindItemRequest request) {

            for (IStackFilter filter : this.filters) {
                if (filter != null && filter.matches(request)) {
                    return true;
                }
            }

            return false;
        }

    }

}
