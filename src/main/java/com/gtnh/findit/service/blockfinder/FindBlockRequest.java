package com.gtnh.findit.service.blockfinder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.gtnh.findit.FindIt;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class FindBlockRequest implements IMessage {

    private Block blockToFind;
    private int metaToFind;
    private boolean isSearchByName;
    private Set<BlockKey> matchingBlocks;

    public static class BlockKey {

        public final int blockId;
        public final int damage;

        public BlockKey(int blockId, int damage) {
            this.blockId = blockId;
            this.damage = damage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BlockKey)) return false;
            BlockKey blockKey = (BlockKey) o;
            return blockId == blockKey.blockId && damage == blockKey.damage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(blockId, damage);
        }
    }

    public FindBlockRequest(Block block, int meta) {
        this.blockToFind = block;
        this.metaToFind = meta;
    }

    public FindBlockRequest(Set<BlockKey> matchingBlocks) {
        this.matchingBlocks = matchingBlocks;
        this.isSearchByName = true;
    }

    public FindBlockRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isSearchByName = buf.readBoolean();
        if (this.isSearchByName) {
            int size = buf.readInt();
            this.matchingBlocks = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                this.matchingBlocks.add(new BlockKey(buf.readShort(), buf.readShort()));
            }
        } else {
            blockToFind = Block.getBlockById(buf.readShort());
            metaToFind = buf.readShort();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isSearchByName);
        if (this.isSearchByName) {
            buf.writeInt(this.matchingBlocks.size());
            for (BlockKey key : this.matchingBlocks) {
                buf.writeShort(key.blockId);
                buf.writeShort(key.damage);
            }
        } else {
            buf.writeShort(Block.getIdFromBlock(blockToFind));
            buf.writeShort(metaToFind);
        }
    }

    public Block getBlockToFind() {
        return blockToFind;
    }

    public int getMetaToFind() {
        return metaToFind;
    }

    public boolean isSearchByName() {
        return this.isSearchByName;
    }

    public boolean matches(Block block, int meta) {
        if (block == null) return false;
        int blockId = Block.getIdFromBlock(block);
        return this.matchingBlocks.contains(new BlockKey(blockId, meta))
                || this.matchingBlocks.contains(new BlockKey(blockId, 32767));
    }

    public static class Handler implements IMessageHandler<FindBlockRequest, BlockFoundResponse> {

        @Override
        public BlockFoundResponse onMessage(FindBlockRequest message, MessageContext ctx) {
            if (message.isSearchByName || (message.blockToFind != null && message.blockToFind != Blocks.air)) {
                FindIt.getBlockFindService().handleRequest(ctx.getServerHandler().playerEntity, message);
            }
            return null;
        }
    }
}
