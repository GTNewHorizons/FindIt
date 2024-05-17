package com.gtnh.findit.service.itemfinder;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.findit.FindIt;
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

    public boolean hasFluidStack() {
        return targetFluidStack != null && targetFluidStack.amount > 0;
    }

    public boolean isFluidSatisfies(FluidStack fluid) {
        if (fluid != null && targetFluidStack != null) {
            return targetFluidStack.isFluidEqual(fluid);
        }
        return false;
    }

    public boolean isStackSatisfies(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        if (hasFluidStack()) {
            return targetFluidStack.isFluidEqual(StackInfo.getFluid(stack));
        }

        return StackInfo.equalItemAndNBT(targetStack, stack, true);
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
