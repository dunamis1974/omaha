package com.stars;

import com.stars.entities.HandOmaha;
import com.stars.entities.Table;
import com.stars.errors.ErrorCard;
import com.stars.errors.ErrorHand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * App start
 * <p>
 * The main method expects 2 parameters
 * 1: input file
 * 2: output file
 */
public class App {
    private String winHi = "%s wins Hi (%s)";
    private String splitHi = "Split Pot Hi (%s)";

    private String winLo = "%s wins Lo (%s)";
    private String splitLo = "Split Pot Lo (%s)";

    public static void main(String[] args) {
        App app = new App();
        app.processFile(args[0], args[1]);
    }

    private void processFile(String inFile, String outFile) {
        System.out.printf("Read from: %s\n\n", inFile);
        String nl = System.lineSeparator();
        Path input = FileSystems.getDefault().getPath(inFile);
        File file = new File(outFile);
        try {
            FileWriter fileWriter = new FileWriter(file);
            Files.lines(input).forEach(line -> {
                String result = this.processLine(line);
                try {
                    fileWriter.write(result);
                    fileWriter.write(nl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("\n\nResult to: %s\n\n", outFile);
    }

    /**
     * Line processing method
     * <p>
     * It gets the full line as string:
     * HandA:Ac-Kd-Jd-3d HandB:5c-5d-6c-7d Board:Ah-Kh-5s-2s-Qd
     *
     * @param line String
     * @return String
     */
    private String processLine(String line) {
        System.out.println(line);

        ProcessorOmaha processor = new ProcessorOmaha();
        String[] hands = line.split("[\\s:]");
        String message;
        try {
            HashMap<String, ? super HandOmaha> handsData = new HashMap<>();
            handsData.put(hands[0], new HandOmaha(hands[1], hands[5]));
            handsData.put(hands[2], new HandOmaha(hands[3], hands[5]));
            Table<ProcessorOmaha> table = new Table(processor, handsData);

            HashMap<String, ? super HandOmaha> tableProcessedData = table.getHands();
            HandOmaha handOne = (HandOmaha) tableProcessedData.get(hands[0]);
            HandOmaha handTwo = (HandOmaha) tableProcessedData.get(hands[2]);

            String hi = evaluateHiHand(hands[0], handOne, hands[2], handTwo);
            String lo = evaluateLoHand(hands[0], handOne, hands[2], handTwo);
            message = String.format("%s; %s", hi, lo);
        } catch (ErrorHand errorHand) {
            message = " Error in hand/board";
        } catch (ErrorCard errorCard) {
            message = " Error in card";
        } catch (Exception e) {
            message = " Unknown error";
        }

        return String.format("%s\n=> %s\n", line, message);
    }

    /**
     * Determine Hi HandOmaha winner
     * <p>
     * Works with already evaluated line.
     *
     * @param handOne HandA evaluated
     * @param handTwo HandB evaluated
     * @return String
     */
    private String evaluateHiHand(String handOneName, HandOmaha handOne, String handTwoName, HandOmaha handTwo) {
        if (handOne.getHiScore() > handTwo.getHiScore()) {
            return String.format(winHi, handOneName, handOne.getRank().getDescription());
        } else if (handOne.getHiScore() < handTwo.getHiScore()) {
            return String.format(winHi, handTwoName, handTwo.getRank().getDescription());
        }
        return String.format(splitHi, handOne.getRank().getDescription());
    }

    /**
     * Determine Lo HandOmaha winner
     * <p>
     * Works with already evaluated line.
     *
     * @param handOne HandA evaluated
     * @param handTwo HandB evaluated
     * @return String
     */
    private String evaluateLoHand(String handOneName, HandOmaha handOne, String handTwoName, HandOmaha handTwo) {
        if (handOne.getLoScore() == null && handTwo.getLoScore() == null) {
            return "No hand qualified for Low";
        }

        Integer loOne = (handOne.getLoScore() == null) ? 100 : handOne.getLoScore();
        Integer loTwo = (handTwo.getLoScore() == null) ? 100 : handTwo.getLoScore();
        if (loOne < loTwo) {
            return String.format(winLo, handOneName, handOne.toStringLoHand());
        } else if (loOne > loTwo) {
            return String.format(winLo, handTwoName, handTwo.toStringLoHand());
        }
        return String.format(splitLo, handOne.toStringLoHand());
    }
}
