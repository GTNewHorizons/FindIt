package com.gtnh.findit.network;

import com.gtnh.findit.FindIt;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class PlzFindItMessage implements IMessage {
    static WeakHashMap<EntityPlayerMP, Long> lastCalled = new WeakHashMap<>();

    private ItemStack stack;

    public PlzFindItMessage(ItemStack stack) {
        this.stack = stack;
    }

    public PlzFindItMessage() {
    }

    public static List<ChunkPosition> getBlockPosInAABB(AxisAlignedBB axisAlignedBB) {
        List<ChunkPosition> blocks = new ArrayList<>();

        int maxY = (int) Math.round(axisAlignedBB.maxY),
            maxX = (int) Math.round(axisAlignedBB.maxX),
            maxZ = (int) Math.round(axisAlignedBB.maxZ);

        for (int y = (int) Math.round(axisAlignedBB.minY); y < maxY; ++y) {
            for (int x = (int) Math.round(axisAlignedBB.minX); x < maxX; ++x) {
                for (int z = (int) Math.round(axisAlignedBB.minZ); z < maxZ; ++z) {
                    blocks.add(new ChunkPosition(x, y, z));
                }
            }
        }
        return blocks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        stack = null;
        try {
            stack = packetBuffer.readItemStackFromBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            packetBuffer.writeItemStackToBuffer(stack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<PlzFindItMessage, KIFoundItMessage> {

        @Override
        public KIFoundItMessage onMessage(PlzFindItMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            World world = player.worldObj;

            // Don't let people spam this, otherwise bad performance and they'll probably get disconnected
            final long time = world.getTotalWorldTime();
            final Long lastCalled = PlzFindItMessage.lastCalled.get(player);
            if (lastCalled != null && time - lastCalled < 10L) {
                return null;
            }

            final Item toFind = message.stack.getItem();
            final  int metaToFind = message.stack.getItemDamage();

            final double x = player.posX,
                         y = player.posY,
                         z = player.posZ;

            int r = FindIt.SEARCH_RADIUS;
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - r, y - r, z -r, x + r, y + r, z + r);

            List<ChunkPosition> posList = new ArrayList<>();
            for (ChunkPosition pos : getBlockPosInAABB(box)) {
                try {
                    final TileEntity tileEntity = world.getTileEntity(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
                    if(tileEntity == null) continue;

                    final Item tileItem = Item.getItemFromBlock(tileEntity.getBlockType());
                    if (tileItem == null) continue;

                    final int tileMeta;
                    if (tileEntity instanceof IGregTechTileEntity)
                        tileMeta = ((IGregTechTileEntity)tileEntity).getMetaTileID();
                    else
                        tileMeta = world.getBlockMetadata(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);

                    if (toFind.equals(tileItem) && metaToFind == tileMeta) {
                        posList.add(pos);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            if (!posList.isEmpty())
                FindIt.NETWORK.sendTo(new KIFoundItMessage(posList), player);

            return null;
        }
    }
}
