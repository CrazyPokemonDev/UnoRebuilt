package de.crazypokemondev.minecraftUnoRebuilt.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.minecraftUnoRebuilt.games.lobby.LobbyState;
import de.crazypokemondev.minecraftUnoRebuilt.games.uno.UnoLobby;
import de.crazypokemondev.minecraftUnoRebuilt.helpers.ArmorStandHelper;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.UUID;

public class PlayerInteractListener implements Listener {
    private final MinecraftUnoRebuilt plugin;

    public PlayerInteractListener(MinecraftUnoRebuilt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getHand() != EquipmentSlot.HAND
                || event.getPlayer().isSneaking())
            return;
        Block clicked = event.getClickedBlock();
        PersistentDataContainer customBlockData = new CustomBlockData(clicked, plugin);
        UUID deckUuid = customBlockData.get(plugin.UNO_DECK_BLOCK_DATA, UUIDDataType.INSTANCE);
        if (deckUuid != null) {
            event.setUseItemInHand(Event.Result.DENY);
            if (!plugin.lobbies.containsKey(deckUuid)) {
                plugin.lobbies.put(deckUuid, new UnoLobby(deckUuid, ArmorStandHelper.getArmorStand(clicked.getWorld(), deckUuid)));
            }
            LobbyState<?> lobby = plugin.lobbies.get(deckUuid);
            lobby.join(event.getPlayer());
        }
    }
}
