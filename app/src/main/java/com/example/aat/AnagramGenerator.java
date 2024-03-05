package com.example.aat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AnagramGenerator {

    public static String generateAnagram(String original) {
        List<Character> chars = new ArrayList<>();
        for (char c : original.toCharArray()) {
            chars.add(c);
        }

        Collections.shuffle(chars, new Random(System.nanoTime()));

        StringBuilder anagram = new StringBuilder(chars.size());
        for (char c : chars) {
            anagram.append(c);
        }

        return anagram.toString();
    }
}
