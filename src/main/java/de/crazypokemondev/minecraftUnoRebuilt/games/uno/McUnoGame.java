package de.crazypokemondev.minecraftUnoRebuilt.games.uno;

import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.minecraftUnoRebuilt.helpers.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitScheduler;
import org.eu.zajc.juno.decks.impl.UnoStandardDeck;
import org.eu.zajc.juno.game.UnoControlledGame;
import org.eu.zajc.juno.game.UnoWinner;
import org.eu.zajc.juno.rules.pack.impl.UnoOfficialRules;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class McUnoGame extends UnoControlledGame {
    private final UnoState state;
    private final ArmorStand armorStand;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public McUnoGame(UnoState state, ArmorStand armorStand) {
        super(UnoStandardDeck.getDeck(), 5, UnoOfficialRules.getPack(), state.getPlayers().toArray(new UnoState.UnoPlayer[0]));
        this.state = state;
        this.armorStand = armorStand;
    }

    @Override
    public void onEvent(@NotNull String format, Object @NotNull ... arguments) {
        scheduler.runTask(MinecraftUnoRebuilt.INSTANCE, () -> {
            getPlayers().forEach(player -> {
                UnoState.UnoPlayer unoPlayer = (UnoState.UnoPlayer) player;
                unoPlayer.getPlayer().sendMessage(String.format(format, arguments));
            });
            armorStand.getEquipment().setHelmet(ItemHelper.fromCard(getTopCard()));
        });
        state.notifyStateChanged();
    }

    @NotNull
    @Override
    public UnoWinner play() {
        UnoWinner winner = super.play();

        state.setFinished();
        switch (winner.getEndReason()) {
            case REQUESTED:
                onEvent("The game was aborted. Maybe someone left unexpectedly?");
                break;
            case FALLBACK:
                onEvent("No more cards to draw! %s wins the game.",
                        Objects.requireNonNull(winner.getWinner()).getName());
                break;
            case VICTORY:
                onEvent("%s won the game!", Objects.requireNonNull(winner.getWinner()).getName());
                break;
            case UNKNOWN:
            default:
                onEvent("Oops, something went wrong. Please contact your server administrator.");
                break;
        }

        scheduler.runTask(MinecraftUnoRebuilt.INSTANCE, () -> armorStand.getEquipment().setHelmet(ItemHelper.createUnoDeckArmorStand()));
        return winner;
    }
}
