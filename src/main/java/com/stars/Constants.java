package com.stars;

import java.util.*;

/**
 * Constants used for hand evaluation
 */
public class Constants {
    /**
     * Double rated card
     */
    public static final String DOUBLE_RATED = "A";
    /**
     * The key for the second rating for double rated cards
     */
    public static final String DOUBLE_RATED_SECOND = "AA";

    /**
     * Card ratings
     */
    public static final Map<String, Integer> RATINGS = new HashMap<String, Integer>() {{
        put("2", 2);
        put("3", 3);
        put("4", 4);
        put("5", 5);
        put("6", 6);
        put("7", 7);
        put("8", 8);
        put("9", 9);
        put("T", 10);
        put("J", 11);
        put("Q", 12);
        put("K", 13);
        put("A", 14);
        put("AA", 1);
    }};

    /**
     * Suit ratings
     * This is not used in Poker for real rating but only for numerical representation of the suit.
     */
    public static final Map<String, Integer> COLORS = new HashMap<String, Integer>() {{
        put("c", 1);
        put("d", 2);
        put("h", 3);
        put("s", 4);
    }};
}
