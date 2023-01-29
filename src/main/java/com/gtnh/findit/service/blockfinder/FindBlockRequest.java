package com.gtnh.findit.service.blockfinder;

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

    public FindBlockRequest(Block block, int meta) {
        this.blockToFind = block;
        this.metaToFind = meta;
    }

    public FindBlockRequest() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        blockToFind = Block.getBlockById(buf.readShort());
        metaToFind = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(Block.getIdFromBlock(blockToFind));
        buf.writeShort(metaToFind);
    }

    public Block getBlockToFind() {
        return blockToFind;
    }

    public int getMetaToFind() {
        return metaToFind;
    }

    public static class Handler implements IMessageHandler<FindBlockRequest, BlockFoundResponse> {

        @Override
        public BlockFoundResponse onMessage(FindBlockRequest message, MessageContext ctx) {
            if (message.blockToFind != null && message.blockToFind != Blocks.air) {
                FindIt.getBlockFindService().handleRequest(ctx.getServerHandler().playerEntity, message);
            }
            return null;
        }
    }
}
