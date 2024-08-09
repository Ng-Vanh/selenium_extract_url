package org.example.TreeDOM;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.HashSet;
import java.util.Set;

public class VectorUtils {
    private static Set<Character> vocabulary = new HashSet<>();

    public static void buildVocabulary(String text) {
        if (text == null) {
            return;
        }
        for (char c : text.toCharArray()) {
            vocabulary.add(c);
        }
    }

    public static RealVector toRealVector(String text) {
        RealVector vector = new ArrayRealVector(vocabulary.size());
        if (text == null) {
            return vector;
        }

        int i = 0;
        for (char c : vocabulary) {
            vector.setEntry(i++, countOccurrences(c, text));
        }
        return vector;
    }

    private static int countOccurrences(char c, String text) {
        int count = 0;
        for (char t : text.toCharArray()) {
            if (c == t) {
                count++;
            }
        }
        return count;
    }

    public static double cosineSimilarity(RealVector v1, RealVector v2) {
        if (v1.getDimension() != v2.getDimension()) {
            throw new IllegalArgumentException("Vectors must be of the same dimension");
        }

        double dotProduct = v1.dotProduct(v2);
        double magnitude1 = v1.getNorm();
        double magnitude2 = v2.getNorm();

        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
            return dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        String s1 = "testscript";
        String s2 = "tedscript";

        // Build vocabulary from both strings
        buildVocabulary(s1);
        buildVocabulary(s2);

        RealVector rv1 = toRealVector(s1);
        RealVector rv2 = toRealVector(s2);

        System.out.println("rv1: " + rv1);
        System.out.println("rv2: " + rv2);
        System.out.println("Cosine Similarity: " + cosineSimilarity(rv1, rv2));
    }
}
