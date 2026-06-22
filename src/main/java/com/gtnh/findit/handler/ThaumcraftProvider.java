package com.gtnh.findit.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.service.itemfinder.FindItemRequest;

import cpw.mods.fml.common.Loader;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ThaumcraftProvider implements IStackFilterProvider {

    public static final boolean ASPECTRECIPEINDEX = Loader.isModLoaded("aspectrecipeindex");
    public static final boolean TCNEIPLUGIN = Loader.isModLoaded("thaumcraftneiplugin");

    static class AspectStackFilter implements IStackFilter {

        final AspectList filterAspects;

        AspectStackFilter(AspectList filterAspects) {
            this.filterAspects = filterAspects;
        }

        @Override
        public boolean matches(FindItemRequest request) {
            if (request.isSearchByName()) {
                if (filterAspects == null || filterAspects.aspects.isEmpty()) return false;
                final java.util.Set<FindItemRequest.ItemKey> matching = request.getMatchingItems();
                if (matching == null || matching.isEmpty()) return false;
                for (FindItemRequest.ItemKey key : matching) {
                    Item matchItem = Item.getItemById(key.itemId);
                    if (matchItem == null) continue;
                    ItemStack dummyStack = new ItemStack(matchItem, 1, key.damage);
                    if (key.tag != null) {
                        dummyStack.setTagCompound(key.tag);
                    }
                    Aspect aspect = null;
                    if (ASPECTRECIPEINDEX && matchItem instanceof ItemAspect) {
                        aspect = ItemAspect.getAspect(dummyStack);
                    } else if (TCNEIPLUGIN && matchItem.getClass().getName()
                            .equals("com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect")) {
                                AspectList stackAspects = com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect
                                        .getAspects(dummyStack);
                                if (stackAspects != null && !stackAspects.aspects.isEmpty()) {
                                    aspect = stackAspects.getAspects()[0];
                                }
                            }
                    if (aspect != null && filterAspects.getAmount(aspect) > 0) {
                        return true;
                    }
                }
                return false;
            }

            ItemStack stack = request.getStackToFind();
            Item item = stack.getItem();
            if (item instanceof IEssentiaContainerItem container) {
                AspectList stackAspects = container.getAspects(stack);
                if (stackAspects == null || stackAspects.aspects.isEmpty()) return false;
                for (Aspect a : stackAspects.getAspects()) {
                    if (filterAspects.getAmount(a) > 0) return true;
                }
                return false;
            }
            Aspect aspect;
            if (ASPECTRECIPEINDEX && item instanceof ItemAspect) {
                aspect = ItemAspect.getAspect(stack);
            } else if (TCNEIPLUGIN && item instanceof com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect) {
                AspectList stackAspects = com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect.getAspects(stack);
                if (stackAspects == null || stackAspects.aspects.isEmpty()) return false;
                aspect = stackAspects.getAspects()[0];
            } else {
                return false;
            }

            return filterAspects.getAmount(aspect) > 0;
        }
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, TileEntity tileEntity) {
        if (!(tileEntity instanceof IAspectContainer aspectContainer)) return null;
        AspectList aspects = aspectContainer.getAspects();
        if (aspects == null) return null;
        return new AspectStackFilter(aspects);
    }

    @Override
    public IStackFilter getFilter(EntityPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof IEssentiaContainerItem containerItem)) return null;
        AspectList aspects = containerItem.getAspects(stack);
        if (aspects == null) return null;
        return new AspectStackFilter(aspects);
    }
}
