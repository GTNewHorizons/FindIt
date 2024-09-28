package com.gtnh.findit.service.itemfinder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

    public FindItemRequest(ItemStack targetStack) {
        this.targetStack = targetStack;
        this.targetFluidStack = StackInfo.getFluid(targetStack);
        this.highlightingEmptyItemStacks = FindItConfig.ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS;
        this.searchInGTPipes = FindItConfig.SEARCH_IN_GT_PIPES;
        this.searchConduits = FindItConfig.SEARCH_IN_ENDERIO_CONDUITS;
    }

    public FindItemRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.targetStack = ProtoUtils.readItemStack(buf);
        this.targetFluidStack = StackInfo.getFluid(targetStack);
        this.highlightingEmptyItemStacks = buf.readBoolean();
        this.searchInGTPipes = buf.readBoolean();
        this.searchConduits = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProtoUtils.writeItemStack(buf, this.targetStack);
        buf.writeBoolean(this.highlightingEmptyItemStacks);
        buf.writeBoolean(this.searchInGTPipes);
        buf.writeBoolean(this.searchConduits);
    }

    public ItemStack getStackToFind() {
        return this.targetStack;
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
            if (message.targetStack != null) {
                FindIt.getItemFindService().handleRequest(ctx.getServerHandler().playerEntity, message);
            }
            return null;
        }
    }
}
