package com.gtnh.findit.service.itemfinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnh.findit.util.ProtoUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class ItemFoundResponse implements IMessage {

    private ItemStack foundStack;
    private boolean isSearchByName;
    private List<FindItemRequest.ItemKey> matchingItems;

    public ItemFoundResponse(ItemStack foundStack) {
        this.foundStack = foundStack;
    }

    public ItemFoundResponse(FindItemRequest request) {
        this.foundStack = request.getStackToFind();
        this.isSearchByName = request.isSearchByName();
        if (request.isSearchByName() && request.getMatchingItems() != null) {
            this.matchingItems = new ArrayList<>(request.getMatchingItems());
        }
    }

    public ItemFoundResponse() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isSearchByName = buf.readBoolean();
        if (this.isSearchByName) {
            int declaredSize = buf.readInt();
            int max = 1000;
            int size = Math.max(0, Math.min(declaredSize, max));
            this.matchingItems = new ArrayList<>(size);
            for (int i = 0; i < declaredSize; i++) {
                int itemId = buf.readInt();
                int damage = buf.readShort();
                NBTTagCompound tag = ByteBufUtils.readTag(buf);
                if (i < max) {
                    this.matchingItems.add(new FindItemRequest.ItemKey(itemId, damage, tag));
                }
            }
        } else {
            this.foundStack = ProtoUtils.readItemStack(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isSearchByName);
        if (this.isSearchByName) {
            int size = this.matchingItems == null ? 0 : this.matchingItems.size();
            buf.writeInt(size);
            if (this.matchingItems != null) {
                for (FindItemRequest.ItemKey item : this.matchingItems) {
                    buf.writeInt(item.itemId);
                    buf.writeShort(item.damage);
                    ByteBufUtils.writeTag(buf, item.tag);
                }
            }
        } else {
            ProtoUtils.writeItemStack(buf, this.foundStack);
        }
    }

    public ItemStack getFoundStack() {
        return foundStack;
    }

    public boolean isSearchByName() {
        return this.isSearchByName;
    }

    public List<FindItemRequest.ItemKey> getMatchingItems() {
        return this.matchingItems;
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
