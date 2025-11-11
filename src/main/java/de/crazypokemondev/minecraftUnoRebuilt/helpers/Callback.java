package de.crazypokemondev.minecraftUnoRebuilt.helpers;

import java.util.concurrent.Semaphore;

public class Callback<T> {
    private T value;
    private final Semaphore semaphore = new Semaphore(0);

    public T get() throws InterruptedException {
        semaphore.acquire();
        return value;
    }

    public void reset() {
        semaphore.drainPermits();
        value = null;
    }

    public void set(T value) {
        this.value = value;
        semaphore.release();
    }
}
