package com.gtnh.findit.service.itemfinder;

import com.gtnh.findit.service.blockfinder.BlockFoundResponse;
import com.gtnh.findit.util.ProtoUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HighlightSlotsPacket implements IMessage {

    private int windowId;
    private ItemStack targetStack;
    private List<Integer> targetSlots;

    public HighlightSlotsPacket(int windowId, ItemStack targetStack, List<Integer> targetSlots) {
        this.windowId = windowId;
        this.targetStack = targetStack;
        this.targetSlots = targetSlots;
    }

    public HighlightSlotsPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        windowId = buf.readInt();
        targetStack = ProtoUtils.readItemStack(buf);

        int slotCount = buf.readInt();
        targetSlots = new ArrayList<>(slotCount);
        for (int i = 0; i < slotCount; i++) {
            targetSlots.add((int) buf.readShort());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        ProtoUtils.writeItemStack(buf, targetStack);

        buf.writeInt(targetSlots.size());
        targetSlots.forEach(buf::writeShort);
    }

    public int getWindowId() {
        return windowId;
    }

    public ItemStack getTargetStack() {
        return targetStack;
    }

    public List<Integer> getTargetSlots() {
        return targetSlots;
    }

    public static class Handler implements IMessageHandler<HighlightSlotsPacket, BlockFoundResponse> {

        @Override
        public BlockFoundResponse onMessage(HighlightSlotsPacket message, MessageContext ctx) {
            if (message.getTargetStack() != null) {
                ClientItemFindService.getInstance().handleSlotHighlight(message);
            }
            return null;
        }
    }
}