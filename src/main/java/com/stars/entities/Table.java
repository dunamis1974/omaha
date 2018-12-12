package com.stars.entities;

import com.stars.interfaces.Hand;
import com.stars.interfaces.Processor;

import java.util.HashMap;
import java.util.Set;

/**
 * Table entity
 * Using generics in order to facilitate different type of
 * processors and Hands on the same table.
 *
 * @param <T>
 */
public class Table<T> {
    private Processor processor;
    private HashMap<String, ? super Hand> hands;

    public Table() {}

    public Table(T processor, HashMap<String, ? super Hand> handsData) throws Exception {
        this.processor = (Processor) processor;
        this.hands = handsData;
        processTable();
    }

    /**
     * Processing table hands with the given processor
     *
     * @throws Exception if processor or hands are not set
     */
    public void processTable() throws Exception {
        if (processor == null || hands == null) {
            throw new Exception("Missing data");
        }

        Set<String> keys = hands.keySet();
        for (String key : keys) {
            Hand hand = (Hand) hands.get(key);
            processor.processHand(hand);
            hands.put(key, hand);
        }
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(T processor) {
        this.processor = (Processor) processor;
    }

    public HashMap<String, ? super Hand> getHands() {
        return hands;
    }

    public void setHands(HashMap<String, ? super Hand> hands) {
        this.hands = hands;
    }
}
