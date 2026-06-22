package com.gtnh.findit.service.itemfinder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.IStackFilter;
import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.util.ProtoUtils;

import codechicken.nei.recipe.StackInfo;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.TileConduitBundle;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import io.netty.buffer.ByteBuf;

public class FindItemRequest implements IMessage {

    private ItemStack targetStack;
    private FluidStack targetFluidStack;
    private boolean highlightingEmptyItemStacks;
    private boolean searchInGTPipes;
    private boolean searchConduits;
    private boolean isSearchByName;
    private Set<ItemKey> matchingItems;

    public static class ItemKey {

        public final int itemId;
        public final int damage;
        public final NBTTagCompound tag;

        public ItemKey(int itemId, int damage) {
            this(itemId, damage, null);
        }

        public ItemKey(int itemId, int damage, NBTTagCompound tag) {
            this.itemId = itemId;
            this.damage = damage;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemKey)) return false;
            ItemKey itemKey = (ItemKey) o;
            return itemId == itemKey.itemId && damage == itemKey.damage && Objects.equals(tag, itemKey.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemId, damage, tag);
        }
    }

    public FindItemRequest(ItemStack targetStack) {
        this.targetStack = targetStack;
        this.targetFluidStack = StackInfo.getFluid(targetStack);
        this.highlightingEmptyItemStacks = FindItConfig.ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS;
        this.searchInGTPipes = FindItConfig.SEARCH_IN_GT_PIPES;
        this.searchConduits = FindItConfig.SEARCH_IN_ENDERIO_CONDUITS;
    }

    public FindItemRequest(Set<ItemKey> matchingItems) {
        this.matchingItems = matchingItems;
        this.isSearchByName = true;
        this.highlightingEmptyItemStacks = FindItConfig.ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS;
        this.searchInGTPipes = FindItConfig.SEARCH_IN_GT_PIPES;
        this.searchConduits = FindItConfig.SEARCH_IN_ENDERIO_CONDUITS;
    }

    public FindItemRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isSearchByName = buf.readBoolean();
        if (this.isSearchByName) {
            int size = buf.readInt();
            this.matchingItems = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                int itemId = buf.readInt();
                int damage = buf.readShort();
                NBTTagCompound tag = ByteBufUtils.readTag(buf);
                this.matchingItems.add(new ItemKey(itemId, damage, tag));
            }
        } else {
            this.targetStack = ProtoUtils.readItemStack(buf);
            this.targetFluidStack = StackInfo.getFluid(targetStack);
        }
        this.highlightingEmptyItemStacks = buf.readBoolean();
        this.searchInGTPipes = buf.readBoolean();
        this.searchConduits = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isSearchByName);
        if (this.isSearchByName) {
            buf.writeInt(this.matchingItems.size());
            for (ItemKey item : this.matchingItems) {
                buf.writeInt(item.itemId);
                buf.writeShort(item.damage);
                ByteBufUtils.writeTag(buf, item.tag);
            }
        } else {
            ProtoUtils.writeItemStack(buf, this.targetStack);
        }
        buf.writeBoolean(this.highlightingEmptyItemStacks);
        buf.writeBoolean(this.searchInGTPipes);
        buf.writeBoolean(this.searchConduits);
    }

    public ItemStack getStackToFind() {
        return this.targetStack;
    }

    public boolean isSearchByName() {
        return this.isSearchByName;
    }

    public Set<ItemKey> getMatchingItems() {
        return this.matchingItems;
    }

    public boolean isItemMatches(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        int id = Item.getIdFromItem(stack.getItem());
        int damage = stack.getItemDamage();
        for (ItemKey key : this.matchingItems) {
            if (key.itemId == id && key.damage == damage) {
                return true;
            }
        }
        return false;
    }

    public boolean isFluidSatisfies(FluidStack fluid) {
        if (fluid == null || !this.highlightingEmptyItemStacks && fluid.amount == 0) return false;
        if (fluid.getFluid() == FluidRegistry.WATER && fluid.amount == 0) return false;

        return this.targetFluidStack != null && this.targetFluidStack.isFluidEqual(fluid);
    }

    public boolean isStackSatisfies(EntityPlayer player, ItemStack stack) {
        if (stack == null || !this.highlightingEmptyItemStacks && stack.stackSize == 0) return false;

        for (IStackFilterProvider provider : FindIt.INSTANCE.pluginsList) {
            IStackFilter filter = provider.getFilter(player, stack);
            if (filter != null && filter.matches(this)) {
                return true;
            }
        }

        return false;
    }

    public boolean isTileSatisfies(EntityPlayer player, TileEntity tileEntity) {
        if (FindIt.isGregTechLoaded() && !this.searchInGTPipes && tileEntity instanceof BaseMetaPipeEntity) {
            return false;
        }

        if (FindIt.isEnderIOLoaded() && !this.searchConduits && tileEntity instanceof TileConduitBundle) {
            return false;
        }

        for (IStackFilterProvider provider : FindIt.INSTANCE.pluginsList) {
            IStackFilter filter = provider.getFilter(player, tileEntity);
            if (filter != null && filter.matches(this)) {
                return true;
            }
        }

        return false;
    }

    public static class Handler implements IMessageHandler<FindItemRequest, BlockFoundResponse> {

        @Override
        public BlockFoundResponse onMessage(FindItemRequest message, MessageContext ctx) {
            if (message.isSearchByName || message.targetStack != null) {
                FindIt.getItemFindService().handleRequest(ctx.getServerHandler().playerEntity, message);
            }
            return null;
        }
    }
}
