package com.gtnh.findit.service.itemfinder;

import net.minecraft.item.ItemStack;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.util.ProtoUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class FindItemRequest implements IMessage {

    private ItemStack stack;

    public FindItemRequest(ItemStack stack) {
        this.stack = stack;
    }

    public FindItemRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ProtoUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProtoUtils.writeItemStack(buf, stack);
    }

    public ItemStack getStackToFind() {
        return stack;
    }

    public static class Handler implements IMessageHandler<FindItemRequest, BlockFoundResponse> {

        @Override
        public BlockFoundResponse onMessage(FindItemRequest message, MessageContext ctx) {
            if (message.stack != null) {
                FindIt.getItemFindService().handleRequest(ctx.getServerHandler().playerEntity, message);
            }
            return null;
        }
    }
}
