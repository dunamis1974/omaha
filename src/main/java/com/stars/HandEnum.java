package com.stars;

/**
 * HandOmaha enum contains the hand ratings
 * and descriptions
 */
public enum HandEnum {
    STRAIGHT_FLUSH("Straight Flush", 9),
    FOUR_OF_KIND("Four of a Kind", 8),
    FULL("Full House", 7),
    FLUSH("Flush", 6),
    STRAIGHT("Straight", 5),
    TREE_OF_KUND("Three of a Kind", 4),
    TWO_PAIR("Two Pair", 3),
    ONE_PAIR("One Pair", 2),
    HIGH_CARD("High Card", 1);

    private final String description;
    private final int rank;

    HandEnum(String description, int rank) {
        this.description = description;
        this.rank = rank;
    }

    public String getDescription() {
        return this.description;
    }

    public int getRank() {
        return this.rank;
    }
}
