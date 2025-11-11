package de.crazypokemondev.minecraftUnoRebuilt.games.lobby;

import de.crazypokemondev.minecraftUnoRebuilt.MinecraftUnoRebuilt;
import de.crazypokemondev.uniGUI.api.GuiState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface GameState extends GuiState {
    BukkitScheduler scheduler = Bukkit.getScheduler();

    void start();

    void abort();

    void join(Player player);

    List<Runnable> finishedHandlers = new CopyOnWriteArrayList<>();

    default void registerFinishedHandler(Runnable runnable) {
        finishedHandlers.add(runnable);
    }

    default void unregisterFinishedHandler(Runnable runnable) {
        finishedHandlers.remove(runnable);
    }

    default void notifyFinished() {
        for (Runnable handler : finishedHandlers) {
            scheduler.runTask(MinecraftUnoRebuilt.INSTANCE, handler);
        }
    }
}
