package com.stars.entities;

import com.stars.HandEnum;
import com.stars.errors.ErrorCard;
import com.stars.errors.ErrorHand;

import java.util.*;

/**
 * Hand evaluation class
 * Uses given hand and the board cards to determine the hand ranking
 */
public class Hand {
    private ArrayList<Card> hand;
    private ArrayList<Card> board;

    private Double hiScore = 0d;
    private ArrayList<Card> hiHand;
    private Integer loScore;
    private ArrayList<Card> loHand;

    private HandEnum rank;

    /**
     * Builds the hand data
     *
     * @param hand  Hand string
     * @param board Board string
     * @throws ErrorHand if error in hand
     * @throws ErrorCard if error in cards
     */
    public Hand(String hand, String board) throws ErrorHand, ErrorCard {
        String[] _hand = hand.split("-");
        String[] _board = board.split("-");
        if (_hand.length == 4 && _board.length == 5) {
            this.hand = this.processCards(_hand);
            this.board = this.processCards(_board);
        } else {
            throw new ErrorHand();
        }
    }

    /**
     * Create list of Card objects
     *
     * @param data Array of all cards in the hand or board
     * @return ArrayList of Cards
     * @throws ErrorCard if error in cards is found
     */
    private ArrayList<Card> processCards(String[] data) throws ErrorCard {
        ArrayList<Card> cards = new ArrayList<>();
        for (String single : data) {
            Card card = new Card(single);
            cards.add(card);
        }

        return cards;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public ArrayList<Card> getBoard() {
        return board;
    }

    public void setBoard(ArrayList<Card> board) {
        this.board = board;
    }

    public Double getHiScore() {
        return hiScore;
    }

    public void setHiScore(Double hiScore) {
        this.hiScore = hiScore;
    }

    public void setHiHand(ArrayList<Card> hiHand) {
        this.hiHand = hiHand;
    }

    public Integer getLoScore() {
        return loScore;
    }

    public void setLoScore(Integer loScore) {
        this.loScore = loScore;
    }

    public void setLoHand(ArrayList<Card> loHand) {
        this.loHand = loHand;
    }

    public void setRank(HandEnum rank) {
        this.rank = rank;
    }

    public HandEnum getRank() {
        return rank;
    }

    /**
     * Return Lo hand as a string
     * This is used for display purposes only
     */
    public String toStringLoHand() {
        ArrayList<Card> hand = loHand;
        hand.sort(Comparator.comparing(Card::getRatingLo));
        StringBuilder cards = new StringBuilder();
        for (Card card : hand) {
            cards.append(card.toString());
        }
        return cards.toString();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("-");
        joiner.add(hiHand.get(0).toString())
                .add(hiHand.get(1).toString())
                .add(hiHand.get(2).toString())
                .add(hiHand.get(3).toString())
                .add(hiHand.get(4).toString());
        return joiner.toString();
    }
}
