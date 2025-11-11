package de.crazypokemondev.minecraftUnoRebuilt.games.uno;

import de.crazypokemondev.minecraftUnoRebuilt.games.lobby.LobbyState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class UnoLobby extends LobbyState<UnoState> {
    private final ArmorStand armorStand;

    public UnoLobby(UUID lobbyUuid, ArmorStand armorStand) {
        super(lobbyUuid);
        this.armorStand = armorStand;
    }

    @Override
    public boolean canStart() {
        return gameState == null && players.size() > 1 && players.size() < 10 && players.stream().allMatch(LobbyPlayer::isReady);
    }

    @Override
    protected @NotNull UnoState createGameState(List<Player> players) {
        UnoState unoState = new UnoState(players);
        McUnoGame game = new McUnoGame(unoState, armorStand);
        unoState.setGame(game);
        return unoState;
    }
}
