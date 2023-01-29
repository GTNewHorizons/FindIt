package com.gtnh.findit.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ChunkPosition;

import io.netty.buffer.ByteBuf;

public class ProtoUtils {

    public static void writePositions(ByteBuf buf, List<ChunkPosition> positions) {
        buf.writeInt(positions.size());
        for (ChunkPosition pos : positions) {
            buf.writeInt(pos.chunkPosX);
            buf.writeByte(pos.chunkPosY);
            buf.writeInt(pos.chunkPosZ);
        }
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            packetBuffer.writeItemStackToBuffer(stack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ChunkPosition> readPositions(ByteBuf buf) {
        int amount = buf.readInt();
        List<ChunkPosition> positions = new ArrayList<>();
        while (amount > 0) {
            final int x = buf.readInt();
            final int y = buf.readUnsignedByte();
            final int z = buf.readInt();
            positions.add(new ChunkPosition(x, y, z));
            --amount;
        }
        return positions;
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            return packetBuffer.readItemStackFromBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
