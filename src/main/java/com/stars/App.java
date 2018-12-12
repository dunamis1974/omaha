package com.stars;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * App start
 *
 * The main method expects 2 parameters
 * 1: input file
 * 2: output file
 */
public class App {
    public static void main(String[] args) {
        System.out.printf("Read from: %s\n\n", args[0]);
        Path input = FileSystems.getDefault().getPath(args[0]);
        if (args.length > 2 && "debug".equals(args[2])) {
            System.out.println(input.toAbsolutePath());
        }
        Processor processor = new Processor();
        String nl = System.lineSeparator();
        File file = new File(args[1]);
        try {
            FileWriter fileWriter = new FileWriter(file);
            Files.lines(input).forEach(line -> {
                String result = processor.processLine(line);
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

        System.out.printf("\n\nResult to: %s\n\n", args[1]);
    }
}
