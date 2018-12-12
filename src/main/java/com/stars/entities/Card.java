package com.stars.entities;

import com.stars.Constants;
import com.stars.errors.ErrorCard;

/**
 * Card entity
 * Holds all data for each card:
 * 1. card data
 * 2. if it is double rated
 * 3. Card rating
 *
 * @see Constants RATINGS
 * 4. Card suit
 * @see Constants COLORS
 */
public class Card {
    private String[] cardData;
    private boolean doubleRated;
    private Integer rating;
    private Integer suit;

    /**
     * Creates instance using card string
     * Example cards:
     * <pre>
     * Ad (Ace diamonds)
     * Kc (King clubs)
     * Qh (Queen hearts)
     * Js (Jack spades)
     * Td (10 diamonds)
     * 9s (9 spades)
     * </pre>
     *
     * @param card Card string 2 letter representation
     * @throws ErrorCard if unknown card is found
     */
    public Card(String card) throws ErrorCard {
        cardData = card.split("");
        rating = Constants.RATINGS.get(cardData[0].toUpperCase());
        suit = Constants.COLORS.get(cardData[1].toLowerCase());
        doubleRated = Constants.DOUBLE_RATED.equals(cardData[0].toUpperCase());
        if (rating == null || suit == null) {
            throw new ErrorCard();
        }
    }

    /**
     * Used to create new Card object with new rating
     * This is helper for double rated cards.
     *
     * @param card    Card object
     * @param newCard New card rating symbol
     */
    public Card(Card card, String newCard) {
        cardData = card.getCardData();
        rating = Constants.RATINGS.get(newCard.toUpperCase());
        suit = card.getSuit();
    }

    /**
     * Card rating
     *
     * @return numeric representation of the card rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Card inverted rating
     *
     * @return numeric representation of the card rating inverted
     */
    public Integer getRatingLo() {
        return rating * -1;
    }

    /**
     * Card data
     *
     * @return full card data
     */
    public String[] getCardData() {
        return cardData;
    }

    /**
     * Suit data
     *
     * @return numerical representation of the suit
     */
    public Integer getSuit() {
        return suit;
    }

    /**
     * Card is double rated or not
     *
     * @return if card is double rated
     */
    public boolean isDoubleRated() {
        return doubleRated;
    }

    /**
     * Card as a string
     *
     * @return string of the card
     */
    @Override
    public String toString() {
        return cardData[0];
    }
}
