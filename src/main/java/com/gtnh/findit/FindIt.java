package com.gtnh.findit;

import java.util.ArrayList;
import java.util.List;

import com.gtnh.findit.IStackFilter.IStackFilterProvider;
import com.gtnh.findit.handler.AdventureBackpackProvider;
import com.gtnh.findit.handler.BackpackProvider;
import com.gtnh.findit.handler.DraconicEvolutionProvider;
import com.gtnh.findit.handler.ForestryStackFilterProvider;
import com.gtnh.findit.handler.MinecraftProvider;
import com.gtnh.findit.handler.ProjectRedExplorationProvider;
import com.gtnh.findit.handler.ThaumcraftProvider;
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

    private boolean isEnderIOLoaded;
    private boolean isExtraUtilitiesLoaded;
    private boolean isGregTechLoaded;

    private SearchCooldownService cooldownService;
    private BlockFindService blockFindService;
    private ItemFindService itemFindService;

    public final List<IStackFilterProvider> pluginsList = new ArrayList<>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.isExtraUtilitiesLoaded = Loader.isModLoaded("ExtraUtilities");
        this.isGregTechLoaded = Loader.isModLoaded("gregtech") && !Loader.isModLoaded("gregapi");
        this.isEnderIOLoaded = Loader.isModLoaded("EnderIO");
        this.cooldownService = new SearchCooldownService();

        FindItConfig.setup(event.getSuggestedConfigurationFile());

        if (event.getSide() == Side.CLIENT) {
            this.blockFindService = new ClientBlockFindService();
            this.itemFindService = new ClientItemFindService();
        } else {
            this.blockFindService = new BlockFindService();
            this.itemFindService = new ItemFindService();
        }

        if (Loader.isModLoaded("Forestry")) {
            this.pluginsList.add(new ForestryStackFilterProvider());
        }

        if (Loader.isModLoaded("adventurebackpack")) {
            this.pluginsList.add(new AdventureBackpackProvider());
        }

        if (Loader.isModLoaded("ProjRed|Exploration")) {
            this.pluginsList.add(new ProjectRedExplorationProvider());
        }

        if (Loader.isModLoaded("DraconicEvolution")) {
            this.pluginsList.add(new DraconicEvolutionProvider());
        }

        if (Loader.isModLoaded("Backpack")) {
            this.pluginsList.add(new BackpackProvider());
        }

        if (Loader.isModLoaded("thaumcraftneiplugin") && Loader.isModLoaded("Thaumcraft")) {
            this.pluginsList.add(new ThaumcraftProvider());
        }

        this.pluginsList.add(new MinecraftProvider());
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

}
