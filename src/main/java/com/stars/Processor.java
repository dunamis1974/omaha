package com.stars;

import com.stars.entities.Card;
import com.stars.entities.Hand;
import com.stars.errors.ErrorCard;
import com.stars.errors.ErrorHand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Processor class
 *
 * The purpose here is to process each line evaluate it and
 * return the final result.
 */
class Processor {
    /**
     * Line processing method
     *
     * It gets the full line as string:
     * HandA:Ac-Kd-Jd-3d HandB:5c-5d-6c-7d Board:Ah-Kh-5s-2s-Qd
     *
     * @param line String
     * @return String
     */
    String processLine (String line) {
        System.out.println(line);
        String[] hands = line.split("[\\s:]");
        String message;
        try {
            Hand handOne = new Hand(hands[1], hands[5]);
            processHand(handOne);

            Hand handTwo = new Hand(hands[3], hands[5]);
            processHand(handTwo);

            String hi = evaluateHiHand(handOne, handTwo);
            String lo = evaluateLoHand(handOne, handTwo);
            message = String.format("%s; %s", hi, lo);
        } catch (ErrorHand errorHand) {
            message = " Error in hand/board";
        } catch (ErrorCard errorCard) {
            message = " Error in card";
        }

        return String.format("%s\n=> %s\n", line, message);
    }

    /**
     * Determine Hi Hand winner
     *
     * Works with already evaluated line.
     *
     * @param handOne HandA evaluated
     * @param handTwo HandB evaluated
     * @return String
     */
    private String evaluateHiHand(Hand handOne, Hand handTwo) {
        if (handOne.getHiScore() > handTwo.getHiScore()) {
            return String.format("HandA wins Hi (%s)", handOne.getRank().getDescription());
        } else if (handOne.getHiScore() < handTwo.getHiScore()) {
            return String.format("HandB wins Hi (%s)", handTwo.getRank().getDescription());
        }
        return String.format("Split Pot Hi (%s)", handOne.getRank().getDescription());
    }

    /**
     * Determine Lo Hand winner
     *
     * Works with already evaluated line.
     *
     * @param handOne HandA evaluated
     * @param handTwo HandB evaluated
     * @return String
     */
    private String evaluateLoHand(Hand handOne, Hand handTwo) {
        if (handOne.getLoScore() == null && handTwo.getLoScore() == null) {
            return "No hand qualified for Low";
        }

        Integer loOne = (handOne.getLoScore() == null) ? 100 : handOne.getLoScore();
        Integer loTwo = (handTwo.getLoScore() == null) ? 100 : handTwo.getLoScore();
        if (loOne < loTwo) {
            return String.format("HandA wins Lo (%s)", handOne.toStringLoHand());
        } else if (loOne > loTwo) {
            return String.format("HandB wins Lo (%s)", handTwo.toStringLoHand());
        }
        return String.format("Split Pot Lo (%s)", handOne.toStringLoHand());
    }


    /**
     * Start the cards processing
     * @param hand Hand to be processed
     */
    private void processHand(Hand hand) {
        ArrayList<Card> evalHand = hand.getHand();
        ArrayList<Card> evalBoard = hand.getBoard();

        for (int i = 0; i < evalHand.size(); i++) {
            for (int j = i + 1; j < evalHand.size(); j++) {
                for (int k = 0; k < evalBoard.size(); k++) {
                    for (int l = k + 1; l < evalBoard.size(); l++) {
                        for (int m = l + 1; m < evalBoard.size(); m++) {
                            ArrayList<Card> currentHand = new ArrayList<>();
                            currentHand.add(evalHand.get(i));
                            currentHand.add(evalHand.get(j));
                            currentHand.add(evalBoard.get(k));
                            currentHand.add(evalBoard.get(l));
                            currentHand.add(evalBoard.get(m));
                            currentHand.sort(Comparator.comparing(Card::getRating));

                            RatingData ratingsData = evaluateKinds(currentHand);
                            processStraightFlush(currentHand, ratingsData, hand);
                            processLowCard(currentHand, hand);
                        }
                    }
                }
            }
        }
    }

    /**
     * Evaluate for Straight Flush
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processStraightFlush(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        boolean found = isSameSuit(currentHand);
        Integer totalRating = null;
        if (found) {
            totalRating = evaluateForStraight(currentHand);
            if (totalRating == null) {
                ArrayList<Card> lowHand = lowerAces(currentHand);
                if (lowHand != null) {
                    totalRating = evaluateForStraight(lowHand);
                }
            }
            found = (totalRating != null);
        }

        HandEnum currentRank = HandEnum.STRAIGHT_FLUSH;
        if (!found) {
            if (currentRank != hand.getRank()) {
                processFourOfAKind(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(totalRating, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for 4 of a kind
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processFourOfAKind(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        boolean found = false;
        Map<Integer, Integer> ratings = ratingsData.ratings;
        if (ratings.size() == 2) {
            for (Map.Entry<Integer, Integer> pair : ratings.entrySet()) {
                if (pair.getValue() == 4) {
                    found = true;
                    break;
                }
            }
        }

        HandEnum currentRank = HandEnum.FOUR_OF_KIND;
        if (!found) {
            if (currentRank != hand.getRank()) {
                processFullHouse(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(ratingsData.ratingTotal, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for Full house
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processFullHouse(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        HandEnum currentRank = HandEnum.FULL;
        if (ratingsData.ratings.size() > 2) {
            if (currentRank != hand.getRank()) {
                processFlush(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(ratingsData.ratingTotal, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for Flush
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processFlush(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        boolean found = isSameSuit(currentHand);

        HandEnum currentRank = HandEnum.FLUSH;
        if (!found) {
            if (currentRank != hand.getRank()) {
                processStraight(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(ratingsData.ratingTotal, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for Straight
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processStraight(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        Integer totalRating = evaluateForStraight(currentHand);
        if (totalRating == null) {
            ArrayList<Card> lowHand = lowerAces(currentHand);
            if (lowHand != null) {
                totalRating = evaluateForStraight(lowHand);
            }
        }

        HandEnum currentRank = HandEnum.STRAIGHT;
        if (totalRating == null) {
            if (currentRank != hand.getRank()) {
                processTreeOfAKind(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(totalRating, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for 3 of a kind
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processTreeOfAKind(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        boolean found = false;
        Map<Integer, Integer> ratings = ratingsData.ratings;
        if (ratings.size() == 3) {
            for (Map.Entry<Integer, Integer> pair : ratings.entrySet()) {
                if (pair.getValue() == 3) {
                    found = true;
                    break;
                }
            }
        }

        HandEnum currentRank = HandEnum.TREE_OF_KUND;
        if (!found) {
            if (currentRank != hand.getRank()) {
                processTwoPair(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(ratingsData.ratingTotal, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for two pairs
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processTwoPair(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        HandEnum currentRank = HandEnum.TWO_PAIR;
        if (ratingsData.ratings.size() > 3) {
            if (currentRank != hand.getRank()) {
                processPair(currentHand, ratingsData, hand);
            }
            return;
        }

        setHandRatings(ratingsData.ratingTotal, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for one pair
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processPair(ArrayList<Card> currentHand, RatingData ratingsData, Hand hand) {
        HandEnum currentRank = HandEnum.ONE_PAIR;
        if (ratingsData.ratings.size() == 4) {
            if (currentRank != hand.getRank()) {
                processHighCard(currentHand, hand);
            }
            return;
        }

        setHandRatings(ratingsData.ratingTotal, currentRank, currentHand, hand);
    }

    /**
     * Evaluate for high card
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processHighCard(ArrayList<Card> currentHand, Hand hand) {
        Integer current = currentHand.get(4).getRating();
        HandEnum currentRank = HandEnum.HIGH_CARD;
        if (current > hand.getHiScore()) {
            hand.setHiScore((double) current);
            hand.setRank(currentRank);
            hand.setHiHand(currentHand);
        }
    }

    /**
     * Evaluate for Low card
     *
     * @param currentHand ArrayList of Cards
     * @param hand Hand to be processed
     */
    private void processLowCard(ArrayList<Card> currentHand, Hand hand) {
        ArrayList<Card> lowHand = lowerAces(currentHand);
        if (lowHand != null) {
            currentHand = lowHand;
        }
        RatingData ratingsData = evaluateKinds(currentHand);
        Integer hiRating = currentHand.get(4).getRating();
        if (hiRating <= 8 && ratingsData.ratings.size() == 5) {
            if (hand.getLoScore() == null) {
                hand.setLoScore(ratingsData.ratingTotal);
                hand.setLoHand(currentHand);
            } else if (hand.getLoScore() > ratingsData.ratingTotal) {
                hand.setLoScore(ratingsData.ratingTotal);
                hand.setLoHand(currentHand);
            }
        }
    }

    /**
     * Set ratings data for the hand
     *
     * @param ratingTotal Total rating of the hand
     * @param currentRank The hand rank
     * @param currentHand Currently evaluated hand
     * @param hand Hand to be processed
     */
    private void setHandRatings(Integer ratingTotal, HandEnum currentRank, ArrayList<Card> currentHand, Hand hand) {
        double totalRating = Math.pow(ratingTotal, currentRank.getRank());
        if (totalRating > hand.getHiScore()) {
            hand.setHiScore(totalRating);
            hand.setRank(currentRank);
            hand.setHiHand(currentHand);
        }
    }

    /**
     * Moves Aces to lower rating
     *
     * @param currentHand ArrayList of Cards
     */
    private ArrayList<Card> lowerAces(ArrayList<Card> currentHand) {
        ArrayList<Card> lowerAcesHand = (ArrayList) currentHand.clone();
        Card lastCard = lowerAcesHand.get(4);
        if (lastCard.isDoubleRated()) {
            lowerAcesHand.add(0, new Card(lastCard, Constants.DOUBLE_RATED_SECOND));
            lowerAcesHand.remove(lowerAcesHand.size() - 1);
            return lowerAcesHand;
        }
        return null;
    }

    /**
     * Evaluate the hand what kinds of cards are found and counts them
     *
     * @param currentHand ArrayList of Cards
     */
    private RatingData evaluateKinds(ArrayList<Card> currentHand) {
        Map<Integer, Integer> ratings = new HashMap<>();
        int total = 0;
        for (Card card : currentHand) {
            Integer rating = card.getRating();
            Integer count = ratings.get(rating);
            count = (count == null) ? 1 : ++count;
            ratings.put(rating, count);
            total += rating;
        }
        return new RatingData(ratings, total);
    }

    /**
     * Evaluate for Straight the determine if strait is present
     *
     * @param currentHand ArrayList of Cards
     */
    private Integer evaluateForStraight(ArrayList<Card> currentHand) {
        Integer prevRating = null;
        Integer totalRating = 0;
        for (Card card : currentHand) {
            Integer rating = card.getRating();
            if (prevRating != null && prevRating != (rating - 1)) {
                return null;
            }
            prevRating = rating;
            totalRating += card.getRating();
        }
        return totalRating;
    }

    /**
     * Check if all cards in the hand are the same suit
     *
     * @param currentHand Current hand evaluated
     * @return boolean
     */
    private boolean isSameSuit(ArrayList<Card> currentHand) {
        Integer mainSuit = currentHand.get(0).getSuit();
        for (Card card : currentHand) {
            Integer suit = card.getSuit();
            if (!suit.equals(mainSuit)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Internal class used for ratings transfer
     */
    private class RatingData {
        /**
         * Number of cards with the same rating
         */
        Map<Integer, Integer> ratings;

        /**
         * Sum of the ratings in the hand
         */
        int ratingTotal;

        RatingData(Map<Integer, Integer> ratings, int ratingTotal) {
            this.ratings = ratings;
            this.ratingTotal = ratingTotal;
        }

    }

}
