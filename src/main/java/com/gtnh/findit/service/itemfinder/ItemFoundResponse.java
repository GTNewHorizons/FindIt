package com.gtnh.findit.service.itemfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.gtnh.findit.util.ProtoUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class ItemFoundResponse implements IMessage {

    private ItemStack foundStack;

    public ItemFoundResponse(ItemStack foundStack) {
        this.foundStack = foundStack;
    }

    public ItemFoundResponse() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        foundStack = ProtoUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProtoUtils.writeItemStack(buf, foundStack);
    }

    public ItemStack getFoundStack() {
        return foundStack;
    }

    public static class Handler implements IMessageHandler<ItemFoundResponse, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ItemFoundResponse message, MessageContext ctx) {
            ClientItemFindService.getInstance().handleResponse(Minecraft.getMinecraft().thePlayer, message);
            return null;
        }
    }

}
