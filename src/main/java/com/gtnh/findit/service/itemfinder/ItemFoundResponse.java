package com.gtnh.findit.service.itemfinder;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.util.ProtoUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class ItemFoundResponse implements IMessage {

    private ItemStack foundStack;
    private List<ChunkPosition> positions;

    public ItemFoundResponse(ItemStack foundStack, List<ChunkPosition> positions) {
        this.foundStack = foundStack;
        this.positions = positions;
    }

    public ItemFoundResponse() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        foundStack = ProtoUtils.readItemStack(buf);
        positions = ProtoUtils.readPositions(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProtoUtils.writeItemStack(buf, foundStack);
        ProtoUtils.writePositions(buf, positions);
    }

    public ItemStack getFoundStack() {
        return foundStack;
    }

    public List<ChunkPosition> getPositions() {
        return positions;
    }

    public static class Handler implements IMessageHandler<ItemFoundResponse, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(ItemFoundResponse message, MessageContext ctx) {
            if (message.foundStack != null && !message.positions.isEmpty()) {
                ClientItemFindService.getInstance().handleResponse(Minecraft.getMinecraft().thePlayer, message);
            }
            return null;
        }
    }

}
