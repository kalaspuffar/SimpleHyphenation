package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ConvertTexToDic {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("ukhyph.tex"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("hyph_en_GB.dic"));

            bw.write("UTF-8");
            bw.newLine();
            bw.write("LEFTHYPHENMIN 2");
            bw.newLine();
            bw.write("RIGHTHYPHENMIN 3");
            bw.newLine();

            boolean exceptions = false;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("}")) {
                    line = line.substring(0, line.indexOf("}"));
                }
                if (line.contains("\\hyp")) {
                    line = line.substring(line.indexOf("\\hyphenation{") + "\\hyphenation{".length());
                    exceptions = true;
                }
                if (line.contains("\\pattern")) {
                    line = line.substring(line.indexOf("\\patterns{") + "\\patterns{".length());
                }

                if (exceptions) {
                    if (line.contains("%")) {
                        line = line.substring(0, line.indexOf("%"));
                    }
                    for (String newWord : handleWords(line)) {
                        bw.write(newWord);
                        bw.newLine();
                    }
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> handleWords(String another) {
        List<String> newWords = new ArrayList<>();
        for (String word : another.split(" ")) {
            if (word.isBlank()) continue;
            String newWord = ".";
            for (String ch : word.split("")) {
                if (ch.equals("-")) {
                    newWord += "9";
                    continue;
                } else if(!newWord.endsWith(".") && !newWord.endsWith("9")) {
                    newWord += "8";
                }
                newWord += ch;
            }
            newWord += ".";
            newWords.add(newWord);
        }
        return newWords;
    }
}