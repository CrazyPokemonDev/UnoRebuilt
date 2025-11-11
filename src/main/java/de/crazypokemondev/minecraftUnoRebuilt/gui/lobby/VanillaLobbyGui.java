package de.crazypokemondev.minecraftUnoRebuilt.gui.lobby;

import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.minecraftUnoRebuilt.games.lobby.LobbyState;
import de.crazypokemondev.uniGUI.api.Gui;
import de.crazypokemondev.uniGUI.api.GuiState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.CloseButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;
import xyz.janboerman.guilib.api.menu.ToggleButton;

import java.util.Iterator;
import java.util.UUID;

public class VanillaLobbyGui extends MenuHolder<MinecraftUnoRebuilt> implements Gui {
    private final LobbyState<?> state;
    private final StartGameButton startGameButton;

    public VanillaLobbyGui(Player ignored, GuiState state) {
        super(MinecraftUnoRebuilt.INSTANCE, InventoryType.CHEST, "Lobby");
        this.state = (LobbyState<?>) state;
        this.state.registerUpdateHandler(this::update);
        setButton(0, new ReadyButton());
        setButton(8, new CloseButton<MinecraftUnoRebuilt>());
        this.startGameButton = new StartGameButton();
        setButton(26, startGameButton);
        update();
    }

    private void update() {
        Iterator<LobbyState.LobbyPlayer> iterator = state.getPlayers().iterator();
        for (int row = 1; row <= 2; row++) {
            for (int col = 2; col < 7; col++) {
                int slot = row * 9 + col;
                if (iterator.hasNext()) {
                    PlayerButton playerButton = new PlayerButton(iterator.next());
                    setButton(slot, playerButton);
                } else {
                    unsetButton(slot);
                }
            }
        }
        startGameButton.update();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (event.getReason().equals(InventoryCloseEvent.Reason.PLUGIN)) return;
        state.getPlayers().removeIf(
                p -> p.getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId()));
        this.state.notifyStateChanged();
    }

    @Override
    public void open(Player player) {
        player.openInventory(this.getInventory());
        if (state.getPlayers().stream().noneMatch(p -> p.getPlayer().getUniqueId().equals(player.getUniqueId()))) {
            state.getPlayers().add(new LobbyState.LobbyPlayer(player, this));
            this.state.notifyStateChanged();
        } else {
            state.setReady(player.getUniqueId(), false);
        }
    }

    @Override
    public void close(Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
    }

    private void onStartButtonClicked() {
        state.start();
    }

    private class ReadyButton extends ToggleButton<VanillaLobbyGui> {
        private static final ItemStack offStack =
                new ItemBuilder(Material.RED_CONCRETE)
                        .name("Not ready")
                        .build();
        private static final ItemStack onStack =
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .name("Ready")
                        .build();

        public ReadyButton() {
            super(offStack);
        }

        @Override
        public void afterToggle(VanillaLobbyGui menuHolder, InventoryClickEvent event) {
            UUID whoClicked = event.getWhoClicked().getUniqueId();
            state.setReady(whoClicked, isEnabled());
        }

        @Override
        public ItemStack updateIcon(VanillaLobbyGui menuHolder, InventoryClickEvent event) {
            return isEnabled() ? onStack : offStack;
        }
    }

    private class StartGameButton extends ItemButton<VanillaLobbyGui> {
        private static final ItemStack canStartIcon = new ItemBuilder(Material.TIPPED_ARROW).name("Start game")
                .changeMeta((PotionMeta meta) -> meta.setBasePotionType(PotionType.LUCK)).build();
        private static final ItemStack cantStartIcon = new ItemBuilder(Material.TIPPED_ARROW).name("Start game")
                .changeMeta((PotionMeta meta) -> meta.setBasePotionType(PotionType.HEALING)).build();

        @Override
        public void onClick(VanillaLobbyGui holder, InventoryClickEvent event) {
            onStartButtonClicked();
        }

        public void update() {
            if (state.canStart()) {
                setIcon(canStartIcon);
            } else {
                setIcon(cantStartIcon);
            }
        }
    }

    private static class PlayerButton extends ItemButton<VanillaLobbyGui> {
        public PlayerButton(LobbyState.LobbyPlayer player) {
            super(new ItemBuilder(Material.PLAYER_HEAD).name(player.getPlayer().getName())
                    .changeMeta((SkullMeta meta) -> meta.setOwningPlayer(player.getPlayer().getPlayer()))
                    .addLore(player.isReady() ? "Ready" : "Not ready").build());
        }
    }
}
