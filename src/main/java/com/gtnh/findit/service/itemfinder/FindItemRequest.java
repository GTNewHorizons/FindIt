package com.gtnh.findit.service.itemfinder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import io.netty.buffer.ByteBuf;

public class FindItemRequest implements IMessage {

    private ItemStack targetStack;
    private FluidStack targetFluidStack;

    public FindItemRequest(ItemStack targetStack) {
        this.targetStack = targetStack;
        this.targetFluidStack = StackInfo.getFluid(targetStack);
    }

    public FindItemRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        targetStack = ProtoUtils.readItemStack(buf);
        targetFluidStack = StackInfo.getFluid(targetStack);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProtoUtils.writeItemStack(buf, targetStack);
    }

    public ItemStack getStackToFind() {
        return targetStack;
    }

    public boolean isFluidSatisfies(FluidStack fluid) {
        if (fluid == null || !FindItConfig.ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS && fluid.amount == 0) return false;

        return targetFluidStack != null && targetFluidStack.isFluidEqual(fluid);
    }

    public boolean isStackSatisfies(EntityPlayer player, ItemStack stack) {
        if (stack == null || !FindItConfig.ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS && stack.stackSize == 0) return false;

        for (IStackFilterProvider provider : FindIt.INSTANCE.pluginsList) {
            IStackFilter filter = provider.getFilter(player, stack);
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
