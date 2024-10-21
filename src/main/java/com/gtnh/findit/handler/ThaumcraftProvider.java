package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.service.itemfinder.FindItemRequest;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ThaumcraftProvider implements IStackFilterProvider {

    static class AspectStackFilter implements IStackFilter {

        AspectList filterAspects;

        AspectStackFilter(AspectList filterAspects) {
            this.filterAspects = filterAspects;
        }

        @Override
        public boolean matches(FindItemRequest request) {
            ItemStack stack = request.getStackToFind();
            Item item = stack.getItem();
            if (!(item instanceof ItemAspect || item instanceof IEssentiaContainerItem)) return false;

            AspectList stackAspects = ItemAspect.getAspects(stack);
            if (stackAspects.size() != 1) return false;

            Aspect stackAspect = stackAspects.getAspects()[0];

            return filterAspects.getAmount(stackAspect) > 0;
        }
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        if (!(tileEntity instanceof IAspectContainer aspectContainer)) return null;
        return new AspectStackFilter(aspectContainer.getAspects());
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof IEssentiaContainerItem containerItem)) return null;
        return new AspectStackFilter(containerItem.getAspects(stack));
    }
}
