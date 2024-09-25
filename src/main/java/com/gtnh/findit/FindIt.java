package com.gtnh.findit;

import com.gtnh.findit.service.blockfinder.BlockFindService;
import com.gtnh.findit.service.blockfinder.ClientBlockFindService;
import com.gtnh.findit.service.cooldown.SearchCooldownService;
import com.gtnh.findit.service.itemfinder.ClientItemFindService;
import com.gtnh.findit.service.itemfinder.ItemFindService;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(
        modid = FindIt.MOD_ID,
        name = FindIt.MOD_NAME,
        version = FindIt.VERSION,
        dependencies = "required-after:NotEnoughItems;after:gregtech",
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class FindIt {

    // Mod info
    public static final String MOD_ID = "findit";
    public static final String MOD_NAME = "FindIt";
    public static final String VERSION = Tags.VERSION;

    @Mod.Instance(MOD_ID)
    public static FindIt INSTANCE;

    private boolean isDraconicEvolutionLoaded;
    private boolean isEnderIOLoaded;
    private boolean isExtraUtilitiesLoaded;
    private boolean isForestryLoaded;
    private boolean isGregTechLoaded;

    private SearchCooldownService cooldownService;
    private BlockFindService blockFindService;
    private ItemFindService itemFindService;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.isExtraUtilitiesLoaded = Loader.isModLoaded("ExtraUtilities");
        this.isGregTechLoaded = Loader.isModLoaded("gregtech");
        this.isEnderIOLoaded = Loader.isModLoaded("EnderIO");
        this.isDraconicEvolutionLoaded = Loader.isModLoaded("DraconicEvolution");
        this.isForestryLoaded = Loader.isModLoaded("Forestry");

        FindItConfig.setup(event.getSuggestedConfigurationFile());
        boolean isClient = event.getSide() == Side.CLIENT;

        this.cooldownService = new SearchCooldownService();
        this.blockFindService = isClient ? new ClientBlockFindService() : new BlockFindService();
        this.itemFindService = isClient ? new ClientItemFindService() : new ItemFindService();
    }

    public static SearchCooldownService getCooldownService() {
        return INSTANCE.cooldownService;
    }

    public static BlockFindService getBlockFindService() {
        return INSTANCE.blockFindService;
    }

    public static ItemFindService getItemFindService() {
        return INSTANCE.itemFindService;
    }

    public static boolean isExtraUtilitiesLoaded() {
        return INSTANCE.isExtraUtilitiesLoaded;
    }

    public static boolean isGregTechLoaded() {
        return INSTANCE.isGregTechLoaded;
    }

    public static boolean isEnderIOLoaded() {
        return INSTANCE.isEnderIOLoaded;
    }

    public static boolean isDraconicEvolutionLoaded() {
        return INSTANCE.isDraconicEvolutionLoaded;
    }

    public static boolean isForestryLoaded() {
        return INSTANCE.isForestryLoaded;
    }
}
