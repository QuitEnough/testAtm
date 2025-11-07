package ru.yana.MultiMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiValueMap {
    public static void main(String[] args) {
        Map<Integer, List<Integer>> multiMap = new HashMap<>();
        multiMap.put(1, Arrays.asList(0, 1, 2));
        multiMap.put(2, Arrays.asList(3, 4, 2));

        System.out.println("initial multiMap:");
        multiMap.forEach((key, values) ->
                System.out.println(key + " -> " + values));

        Map<Integer, Integer> reversedMap = multiMap.entrySet()
                .stream()
                .flatMap(entry -> {
                    Integer key = entry.getKey();
                    List<Integer> values = entry.getValue();
                    return values
                            .stream()
                            .map(value -> Map.entry(value, key));
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existingValue, newValue) -> existingValue
                ));

        System.out.println("\nreversed map:");
        reversedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}
