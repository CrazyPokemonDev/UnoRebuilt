package de.crazypokemondev.minecraftUnoRebuilt.games.lobby;

import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.uniGUI.api.Gui;
import de.crazypokemondev.uniGUI.api.GuiState;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Slf4j
public abstract class LobbyState<GS extends GameState> implements GuiState {
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();
    protected final UUID lobbyUuid;
    protected final List<LobbyPlayer> players = new ArrayList<>();
    @Nullable
    protected GS gameState = null;

    public LobbyState(UUID lobbyUuid) {
        this.lobbyUuid = lobbyUuid;
    }

    public void start() {
        if (!canStart()) return;
        gameState = createGameState(players.stream().map(LobbyPlayer::getPlayer).toList());

        scheduler.runTask(MinecraftUnoRebuilt.INSTANCE, () ->
                players.forEach(
                        player -> player.getGui().close(player.getPlayer())
                ));
        gameState.registerFinishedHandler(this::cleanUp);
        gameState.start();
    }

    public void join(Player player) {
        if (gameState == null) {
            Gui gui = MinecraftUnoRebuilt.GUI_HANDLER.openGui(player, MinecraftUnoRebuilt.GuiIds.LOBBY, this);
            if (players.stream().noneMatch(p -> p.getPlayer().getUniqueId().equals(player.getUniqueId()))) {
                players.add(new LobbyState.LobbyPlayer(player, gui));
            } else {
                setReady(player.getUniqueId(), false);
            }
            notifyStateChanged();
        } else {
            gameState.join(player);
        }
    }

    public void setReady(UUID playerUuid, boolean ready) {
        players.stream()
                .filter(p -> p.getPlayer().getUniqueId().equals(playerUuid))
                .forEach(p -> p.setReady(ready));
        notifyStateChanged();
    }

    public void abort() {
        if (gameState == null) {
            scheduler.runTask(MinecraftUnoRebuilt.INSTANCE, () -> players.forEach(player -> player.gui.close(player.getPlayer())));
        } else {
            gameState.abort();
        }
        cleanUp();
    }

    protected void cleanUp() {
        MinecraftUnoRebuilt.INSTANCE.lobbies.remove(lobbyUuid);
    }

    public abstract boolean canStart();

    protected abstract @NotNull GS createGameState(List<Player> players);

    @Getter
    public static class LobbyPlayer {
        private final Player player;
        private final Gui gui;
        @Setter
        private boolean ready;

        public LobbyPlayer(Player player, Gui gui) {
            this.player = player;
            this.gui = gui;
        }
    }
}
