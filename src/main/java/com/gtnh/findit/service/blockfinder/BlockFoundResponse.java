package com.gtnh.findit.service.blockfinder;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkPosition;

import com.gtnh.findit.util.ProtoUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class BlockFoundResponse implements IMessage {

    private List<ChunkPosition> positions;

    public BlockFoundResponse(List<ChunkPosition> positions) {
        this.positions = positions;
    }

    public BlockFoundResponse() {}

    public List<ChunkPosition> getPositions() {
        return positions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        positions = ProtoUtils.readPositions(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProtoUtils.writePositions(buf, positions);
    }

    public static class Handler implements IMessageHandler<BlockFoundResponse, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(BlockFoundResponse message, MessageContext ctx) {
            ClientBlockFindService.getInstance().handleResponse(Minecraft.getMinecraft().thePlayer, message);
            return null;
        }
    }
}
