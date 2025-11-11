package de.crazypokemondev.minecraftUnoRebuilt.helpers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ArmorStandHelper {

    public static ArmorStand createArmorStand(World world, Location location) {
        Location armorStandLoc = location.clone().add(-0.06, -1.1, 1.06);

        ArmorStand armorStand = (ArmorStand) world.spawnEntity(armorStandLoc, EntityType.ARMOR_STAND);
        armorStand.setRotation(45, 0);
        armorStand.setHeadPose(new EulerAngle(-Math.PI / 2, 0, 0));
        armorStand.setItem(EquipmentSlot.HEAD, ItemHelper.createUnoDeckArmorStand());

        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setPersistent(true);
        armorStand.setSilent(true);

        armorStand.addScoreboardTag("uno_game");

        return armorStand;
    }

    public static void removeArmorStand(World world, @NotNull UUID deckUuid) {
        Entity armorStand = world.getEntity(deckUuid);
        if (armorStand != null) armorStand.remove();
    }

    public static ArmorStand getArmorStand(World world, @NotNull UUID deckUuid) {
        return (ArmorStand) world.getEntity(deckUuid);
    }
}
