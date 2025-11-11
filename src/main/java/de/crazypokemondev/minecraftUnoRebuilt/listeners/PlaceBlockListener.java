package de.crazypokemondev.minecraftUnoRebuilt.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.minecraftUnoRebuilt.helpers.ArmorStandHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import static de.crazypokemondev.minecraftUnoRebuilt.helpers.ItemHelper.isUnoDeck;

public class PlaceBlockListener implements Listener {
    private final MinecraftUnoRebuilt plugin;

    public PlaceBlockListener(MinecraftUnoRebuilt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        ItemStack inHand = event.getItemInHand();
        if (!isUnoDeck(inHand) || event.isCancelled()) return;
        Block blockPlaced = event.getBlockPlaced();

        Block block = event.getBlock();
        World world = block.getWorld();
        Location blockLoc = block.getLocation();
        ArmorStand armorStand = ArmorStandHelper.createArmorStand(world, blockLoc);

        PersistentDataContainer customBlockData = new CustomBlockData(blockPlaced, plugin);
        customBlockData.set(plugin.UNO_DECK_BLOCK_DATA, UUIDDataType.INSTANCE, armorStand.getUniqueId());
    }
}
