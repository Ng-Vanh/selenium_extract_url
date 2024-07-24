package org.example.generator;

public class TokenInfo {
    private String word;
    private String pos;

    public TokenInfo(String word, String pos) {
        this.word = word;
        this.pos = pos;
    }

    public String getWord() {
        return word;
    }

    public String getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return word + ": " + pos;
    }
}
