package com.gtnh.findit;

import com.gtnh.findit.proxy.FindItCommon;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

@Mod(
        modid = FindIt.MOD_ID,
        name = FindIt.MOD_NAME,
        version = FindIt.VERSION,
        dependencies = "required-after:NotEnoughItems;required-after:gregtech"
)
public class FindIt {

    // Mod info
    public static final String MOD_ID = "findit";
    public static final String MOD_NAME = "FindIt";
    public static final String VERSION = "1.0.0";

    // Config
    public static int SEARCH_RADIUS = 16;

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static FindIt INSTANCE;

    @SidedProxy(serverSide = "com.gtnh.findit.proxy.FindItCommon", clientSide = "com.gtnh.findit.proxy.FindItClient")
    public static FindItCommon proxy;


    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        this.setupConfig(event.getSuggestedConfigurationFile());
        proxy.preinit(event);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        proxy.postinit(event);
    }

    public void setupConfig(final File file) {
        final Configuration config = new Configuration(file);
        try {
            // Load config
            config.load();

            // Read props from config
            Property searchRadiousProp = config.get(Configuration.CATEGORY_GENERAL,
                    "SearchRadius", // Property name
                    "16", // Default value
                    "Radius to search within"); // Comment

            FindIt.SEARCH_RADIUS = searchRadiousProp.getInt();
        } catch (Exception e) {
            // Failed reading/writing, just continue
        } finally {
            // Save props to config IF config changed
            if (config.hasChanged()) config.save();
        }
    }
}
