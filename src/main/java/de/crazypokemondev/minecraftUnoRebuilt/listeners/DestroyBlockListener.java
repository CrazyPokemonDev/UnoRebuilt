package de.crazypokemondev.minecraftUnoRebuilt.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.jeff_media.customblockdata.CustomBlockData;
import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.minecraftUnoRebuilt.games.lobby.LobbyState;
import de.crazypokemondev.minecraftUnoRebuilt.helpers.ArmorStandHelper;
import de.crazypokemondev.minecraftUnoRebuilt.helpers.ItemHelper;
import io.papermc.paper.entity.PlayerGiveResult;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DestroyBlockListener implements Listener {
    private final MinecraftUnoRebuilt plugin;

    public DestroyBlockListener(MinecraftUnoRebuilt plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDestroyedBlock(BlockBreakEvent event) {
        if (cleanGameForBrokenTable(event.getBlock(), !event.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onDestroyedBlock(BlockDestroyEvent event) {
        if (cleanGameForBrokenTable(event.getBlock(), true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        cleanGameForBrokenTable(event.getBlock(), false);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().forEach(block -> cleanGameForBrokenTable(block, false));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().forEach(block -> cleanGameForBrokenTable(block, false));
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.getBlocks().stream().anyMatch(block -> new CustomBlockData(block, plugin).has(plugin.UNO_DECK_BLOCK_DATA))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlocks().stream().anyMatch(block -> new CustomBlockData(block, plugin).has(plugin.UNO_DECK_BLOCK_DATA))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickBlock(PlayerPickBlockEvent event) {
        Player player = event.getPlayer();
        if (new CustomBlockData(event.getBlock(), plugin).has(plugin.UNO_DECK_BLOCK_DATA)) {
            int sourceSlot = -1;
            for (Map.Entry<Integer, ? extends ItemStack> entry : player.getInventory().all(Material.RED_CARPET).entrySet()) {
                if (ItemHelper.isUnoDeck(entry.getValue())) {
                    sourceSlot = entry.getKey();
                    break;
                }
            }
            if (sourceSlot == -1 && player.getGameMode().equals(GameMode.CREATIVE)) {
                ItemStack deck = ItemHelper.createUnoDeck();
                PlayerGiveResult result = player.give(List.of(deck), false);
                if (!result.leftovers().isEmpty()) {
                    int targetSlot = event.getTargetSlot();
                    player.getInventory().setItem(targetSlot, deck);
                    sourceSlot = targetSlot;
                } else {
                    for (Map.Entry<Integer, ? extends ItemStack> entry : player.getInventory().all(Material.RED_CARPET).entrySet()) {
                        if (ItemHelper.isUnoDeck(entry.getValue())) {
                            sourceSlot = entry.getKey();
                            break;
                        }
                    }
                }
            }
            event.setSourceSlot(sourceSlot);
            if (sourceSlot >= 0 && sourceSlot < 9) event.setTargetSlot(sourceSlot);
        }
    }


    private boolean cleanGameForBrokenTable(Block block, boolean dropItem) {
        CustomBlockData blockData = new CustomBlockData(block, plugin);
        UUID deckUuid = blockData.get(plugin.UNO_DECK_BLOCK_DATA, UUIDDataType.INSTANCE);
        if (deckUuid != null) {
            block.setType(Material.AIR);
            if (dropItem) block.getWorld().dropItemNaturally(block.getLocation(), ItemHelper.createUnoDeck());
            blockData.clear();

            ArmorStandHelper.removeArmorStand(block.getWorld(), deckUuid);

            LobbyState<?> lobby = plugin.lobbies.remove(deckUuid);
            if (lobby != null) {
                lobby.abort();
            }

            return true;
        }
        return false;
    }
}
