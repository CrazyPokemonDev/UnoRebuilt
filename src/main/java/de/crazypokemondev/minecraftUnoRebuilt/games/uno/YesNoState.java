package de.crazypokemondev.minecraftUnoRebuilt.games.uno;

import de.crazypokemondev.uniGUI.api.GuiState;
import org.eu.zajc.juno.cards.UnoCard;

public record YesNoState(UnoState.UnoPlayer player, UnoCard card) implements GuiState {
}
