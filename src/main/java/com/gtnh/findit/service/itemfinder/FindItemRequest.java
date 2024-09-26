package com.gtnh.findit.service.itemfinder;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.util.ProtoUtils;
import com.gtnh.findit.util.mods.ForestryUtils;

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
        return targetFluidStack != null;
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

        // Additionally check for backpacks that might contain the item itself.
        if (FindIt.isForestryLoaded()) {
            Optional<Boolean> result = ForestryUtils.getInventoryOfPotentialStorageItem(stack).map(inventory -> {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    // This does a recursive call, but backpacks cannot store other backpacks, so we won't run into
                    // StackOverflowExceptions.
                    if (inventory.getStackInSlot(i) != null && isStackSatisfies(inventory.getStackInSlot(i))) {
                        return true;
                    }
                }

                // None of the inventory slots contained the item we were looking for.
                return false;
            });
            if (result.isPresent()) {
                return result.get();
            }
        }

        return StackInfo.equalItemAndNBT(targetStack, stack, true) && shouldHighlightItemStack(stack);
    }

    /**
     * Returns whether an {@code ItemStack} should be highlighted inside inventories. The method explicitly checks for
     * empty item stacks and consults the mod's configuration to determine whether empty item stacks should be
     * highlighted or not.
     *
     * @param itemStack The {@code ItemStack} to check.
     * @return {@code true} if the {@code ItemStack} should be highlighted, {@code false} otherwise.
     */
    private boolean shouldHighlightItemStack(ItemStack itemStack) {
        // If the user requested to highlight empty item stacks, we early return to make sure that we do so.
        if (FindItConfig.ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS) {
            return true;
        }

        return itemStack.stackSize > 0;
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
