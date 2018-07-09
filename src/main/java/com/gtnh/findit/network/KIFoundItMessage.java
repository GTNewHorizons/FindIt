package com.gtnh.findit.network;

import com.gtnh.findit.proxy.client.ParticlePosition;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class KIFoundItMessage implements IMessage {

    private List<ChunkPosition> positions;

    public KIFoundItMessage(List<ChunkPosition> positions) {
        this.positions = positions;
    }

    public KIFoundItMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        int amount = packetBuffer.readInt();
        positions = new ArrayList<>();
        while (amount > 0) {
            final int x = packetBuffer.readInt();
            final int y = packetBuffer.readUnsignedByte();
            final int z = packetBuffer.readInt();
            positions.add(new ChunkPosition(x, y, z));
            --amount;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeInt(positions.size());
        for (ChunkPosition pos : positions) {
            packetBuffer.writeInt(pos.chunkPosX);
            packetBuffer.writeByte(pos.chunkPosY);
            packetBuffer.writeInt(pos.chunkPosZ);
        }
    }

    public static class Handler implements IMessageHandler<KIFoundItMessage, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(KIFoundItMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
            World world = Minecraft.getMinecraft().thePlayer.worldObj;
            for (ChunkPosition pos : message.positions) {
                for (int i = 0; i < 2; ++i)
                    Minecraft.getMinecraft().effectRenderer.addEffect(
                        new ParticlePosition(
                            Minecraft.getMinecraft().thePlayer.worldObj,
                           pos.chunkPosX + (0.5 + (world.rand.nextDouble() - 0.5) * world.rand.nextDouble()),
                           pos.chunkPosY + (0.5 + (world.rand.nextDouble() - 0.5) * world.rand.nextDouble()),
                           pos.chunkPosZ + (0.5 + (world.rand.nextDouble() - 0.5) * world.rand.nextDouble())
                        )
                    );
            }
            return null;
        }
    }

}
