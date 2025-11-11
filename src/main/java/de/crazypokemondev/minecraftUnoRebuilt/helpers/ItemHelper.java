package de.crazypokemondev.minecraftUnoRebuilt.helpers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.eu.zajc.juno.cards.UnoCard;
import org.eu.zajc.juno.cards.UnoCardColor;
import org.eu.zajc.juno.cards.impl.UnoDrawCard;
import org.eu.zajc.juno.cards.impl.UnoNumericCard;
import org.eu.zajc.juno.cards.impl.UnoReverseCard;
import org.eu.zajc.juno.cards.impl.UnoSkipCard;
import xyz.janboerman.guilib.api.ItemBuilder;

import java.util.Base64;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ItemHelper {
    public static final String UNO_DECK_IDENTIFIER = "unodeck";
    public static final String UNO_MODEL_IDENTIFIER = "uno";
    private static final CustomModelData CUSTOM_MODEL_DATA_DRAW_PILE = CustomModelData.customModelData()
            .addString(UNO_MODEL_IDENTIFIER)
            .build();

    public static ItemStack createUnoDeck() {
        ItemStack stack = new ItemBuilder(Material.RED_CARPET).name("UNO Deck").build();
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(UNO_DECK_IDENTIFIER).build());
        return stack;
    }

    public static ItemStack createUnoDeckArmorStand() {
        ItemStack stack = new ItemStack(Material.PAPER);
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(UNO_DECK_IDENTIFIER).build());
        return stack;
    }

    public static boolean isUnoDeck(ItemStack stack) {
        CustomModelData data = stack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
        return stack.getType() == Material.RED_CARPET && data != null && !data.strings().isEmpty() && data.strings().getFirst().equals(UNO_DECK_IDENTIFIER);
    }

    public static ItemStack getSkull(String url, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty()) return head;

        head.editMeta(SkullMeta.class, skullMeta -> {
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, "");

            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
            playerProfile.setProperty(new ProfileProperty("textures", new String(encodedData)));

            skullMeta.setPlayerProfile(playerProfile);
            skullMeta.displayName(Component.text(name));
        });

        return head;
    }

    public static ItemStack getSkullUp() {
        return getSkull("http://textures.minecraft.net/texture/9cdb8f43656c06c4e8683e2e6341b4479f157f48082fea4aff09b37ca3c6995b", "Scroll Up");
    }

    public static ItemStack getSkullDown() {
        return getSkull("http://textures.minecraft.net/texture/61e1e730c77279c8e2e15d8b271a117e5e2ca93d25c8be3a00cc92a00cc0bb85", "Scroll Down");
    }

    public static ItemStack fromCard(UnoCard card) {
        if (card == null) return ItemStack.empty();
        ItemStack stack = new ItemBuilder(Material.PAPER).name(getCardName(card)).build();
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, getCustomModelDataForCard(card));
        return stack;
    }

    public static ItemStack drawPileIcon() {
        ItemStack stack = new ItemBuilder(Material.PAPER).name("Draw").build();
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CUSTOM_MODEL_DATA_DRAW_PILE);
        return stack;
    }

    private static String getCardName(UnoCard card) {
        return (card.getOriginalColor().equals(UnoCardColor.WILD) && !card.getColor().equals(UnoCardColor.WILD) ? card.getColor() + " " : "") + card;
    }

    private static CustomModelData getCustomModelDataForCard(UnoCard card) {
        return CustomModelData.customModelData()
                .addString(UNO_MODEL_IDENTIFIER)
                .addColor(getColor(card.getOriginalColor()))
                .addString(card.getColor().toString())
                .addString(getCardValue(card))
                .addColor(getTextColor(card))
                .build();
    }

    private static Color getColor(UnoCardColor originalColor) {
        return switch (originalColor) {
            case RED -> Color.fromRGB(255, 0, 0);
            case GREEN -> Color.fromRGB(38, 151, 0);
            case BLUE -> Color.fromRGB(72, 0, 255);
            case YELLOW -> Color.fromRGB(255, 236, 0);
            case WILD -> Color.BLACK;
        };
    }

    private static Color getTextColor(UnoCard card) {
        if (card.getColor().equals(UnoCardColor.YELLOW)
                || (card.getColor().equals(UnoCardColor.WILD) && card instanceof UnoDrawCard)) return Color.BLACK;
        else return Color.WHITE;
    }

    private static String getCardValue(UnoCard card) {
        return switch (card) {
            case UnoNumericCard numeric -> String.valueOf(numeric.getNumber());
            case UnoDrawCard draw -> "+" + draw.getAmount();
            case UnoReverseCard ignored -> "reverse";
            case UnoSkipCard ignored -> "skip";
            default -> "wild";
        };
    }
}
