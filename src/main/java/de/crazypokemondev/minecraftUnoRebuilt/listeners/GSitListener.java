package de.crazypokemondev.minecraftUnoRebuilt.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import dev.geco.gsit.api.event.PreEntitySitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;

public class GSitListener implements Listener {
    private final MinecraftUnoRebuilt plugin;

    public GSitListener(MinecraftUnoRebuilt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreEntitySit(PreEntitySitEvent event) {
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), plugin);
        if (customBlockData.has(plugin.UNO_DECK_BLOCK_DATA)) {
            event.setCancelled(true);
        }
    }
}
