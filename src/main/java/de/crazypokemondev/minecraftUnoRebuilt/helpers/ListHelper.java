package de.crazypokemondev.minecraftUnoRebuilt.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ListHelper {
    private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new),
            list -> {
                Collections.shuffle(list);
                return Collections.unmodifiableList(list);
            }
    );

    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, List<T>> toUnmodifiableShuffledList() {
        return (Collector<T, ?, List<T>>) SHUFFLER;
    }
}
