package com.gtnh.findit.service.cooldown;

import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnh.findit.FindItConfig;

public class SearchCooldownService {

    private final WeakHashMap<EntityPlayerMP, Long> lastSearchTime = new WeakHashMap<>();

    /**
     * @return true if player has active cooldown
     */
    public boolean checkSearchCooldown(EntityPlayerMP player) {
        long time = player.getServerForPlayer().func_73046_m().getTickCounter();
        Long lastTime = lastSearchTime.get(player);

        if (lastTime != null && time - lastTime < FindItConfig.SEARCH_COOLDOWN) {
            return true;
        }
        lastSearchTime.put(player, time);
        return false;
    }
}
