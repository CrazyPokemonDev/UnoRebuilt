package de.crazypokemondev.minecraftUnoRebuilt.gui.uno;

import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.minecraftUnoRebuilt.games.uno.UnoState;
import de.crazypokemondev.minecraftUnoRebuilt.helpers.ItemHelper;
import de.crazypokemondev.uniGUI.api.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.eu.zajc.juno.cards.UnoCard;
import org.jetbrains.annotations.Nullable;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.Iterator;

public abstract class VanillaUnoCardsScreen extends MenuHolder<MinecraftUnoRebuilt> implements Gui {
    protected final ScrollButton pageButton;
    protected int cardOffset = 0;

    public VanillaUnoCardsScreen(String title) {
        super(MinecraftUnoRebuilt.INSTANCE, 54, title);

        this.pageButton = new ScrollButton(new ItemBuilder(Material.LADDER).name("1").build(), 0);
        setButton(35, new ScrollButton(ItemHelper.getSkullUp(), -1));
        setButton(44, pageButton);
        setButton(53, new ScrollButton(ItemHelper.getSkullDown(), 1));
    }

    protected void setUnoCardButtons() {
        UnoState.UnoPlayer player = getPlayer();
        if (player != null) {
            Iterator<UnoCard> cards = player.getCards().iterator();
            for (int i = 0; i < cardOffset * 8; i++) {
                if (!cards.hasNext()) break;
                cards.next();
            }
            for (int i = 27; i < 54; i++) {
                if (i % 9 == 8) continue;
                if (cards.hasNext()) {
                    setButton(i, createCardButton(cards.next()));
                } else {
                    unsetButton(i);
                }
            }
        }
    }

    @Override
    public void open(Player player) {
        player.openInventory(this.getInventory());
    }

    @Override
    public void close(Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
    }

    protected class ScrollButton extends ItemButton<VanillaUnoCardsScreen> {
        private final int offset;

        public ScrollButton(ItemStack icon, int offset) {
            super(icon);
            this.offset = offset;
        }

        @Override
        public void onClick(VanillaUnoCardsScreen holder, InventoryClickEvent event) {
            UnoState.UnoPlayer player = getPlayer();
            if (player == null || offset == 0 || cardOffset + offset < 0 || cardOffset + offset > (player.getHandSize() / 8) - 2)
                return;
            cardOffset += offset;
            ItemStack icon = pageButton.getIcon();
            icon.setAmount(cardOffset + 1);
            icon.getItemMeta().displayName(Component.text(cardOffset + 1));
            pageButton.setIcon(icon);
            setUnoCardButtons();
        }
    }

    protected abstract @Nullable UnoState.UnoPlayer getPlayer();

    protected abstract MenuButton<? extends VanillaUnoCardsScreen> createCardButton(UnoCard card);
}
