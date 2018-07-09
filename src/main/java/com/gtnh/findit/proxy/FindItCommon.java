package com.gtnh.findit.proxy;

import com.gtnh.findit.network.KIFoundItMessage;
import com.gtnh.findit.network.PlzFindItMessage;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

import static com.gtnh.findit.FindIt.NETWORK;

public class FindItCommon {


    public void preinit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(PlzFindItMessage.Handler.class, PlzFindItMessage.class, 0, Side.SERVER);
        NETWORK.registerMessage(KIFoundItMessage.Handler.class, KIFoundItMessage.class, 1, Side.CLIENT);
    }


    public void init(FMLInitializationEvent event) {

    }


    public void postinit(FMLPostInitializationEvent event) {

    }
}
