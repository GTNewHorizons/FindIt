package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
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

        AspectList aspects;

        AspectStackFilter(AspectList aspects) {
            this.aspects = aspects;
        }

        @Override
        public boolean matches(FindItemRequest request) {
            ItemStack stack = request.getStackToFind();
            if (!(stack.getItem() instanceof ItemAspect)) return false;

            // An ItemAspect from ThaumcraftNeiPlugin never has multiple aspects.
            Aspect stackAspect = ItemAspect.getAspects(stack).getAspects()[0];
            if (stackAspect == null) return false;

            return aspects.getAmount(stackAspect) > 0;
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
