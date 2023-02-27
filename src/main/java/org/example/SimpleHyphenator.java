package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleHyphenator {

    private final Map<String, short[]> hyphenWords = new HashMap<>();
    private int maxWordLen = 0;
    private int leftHyphenMin = 1;
    private int rightHyphenMin = 1;
    private final Map<String, String> hyphCache = new HashMap<>();

    SimpleHyphenator(String locale) throws Exception {
        try {
            ResourceBundle p = new PropertyResourceBundle(
                new FileReader("src/main/java/org/example/SimpleHyphenator.properties")
            );
            String path = "dictionaries";
            loadTable(new FileReader(new File(path, p.getString(locale))));
        } catch (Exception e) {
            throw e;
        }
    }
    public void loadTable(FileReader fr) throws Exception {
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("%")) {
                line = line.substring(0, line.indexOf("%"));
            }
            if (
                line.isEmpty() ||
                line.trim().equals("") ||
                line.toUpperCase().contains("COMPOUNDLEFTHYPHENMIN") ||
                line.toUpperCase().contains("COMPOUNDRIGHTHYPHENMIN") ||
                line.toUpperCase().contains("ISO8859-1") ||
                line.toUpperCase().contains("UTF-8")
            ) {
                continue;
            }
            if (line.toUpperCase().startsWith("LEFTHYPHENMIN")) {
                leftHyphenMin = Integer.parseInt(line.split(" ")[1]);
                continue;
            }
            if (line.toUpperCase().startsWith("RIGHTHYPHENMIN")) {
                rightHyphenMin = Integer.parseInt(line.split(" ")[1]);
                continue;
            }

            String simpleWord = simpleWord(line);
            maxWordLen = Math.max(maxWordLen, simpleWord.length());
            hyphenWords.put(simpleWord, shortMask(line, simpleWord.length()));
        }
    }

    public List<MaskPiece> findMaskPieces(String word) {
        List<MaskPiece> allPieces = new ArrayList<>();

        if (hyphenWords.containsKey(word)) {
            allPieces.add(new MaskPiece(0, hyphenWords.get(word)));
        }
        for (int wLen = 1; wLen < Math.min(word.length(), maxWordLen); wLen++) {
            for (int pos = 0; pos < word.length() - wLen + 1; pos++) {
                String wordPiece = word.substring(pos, pos + wLen);
                if (hyphenWords.containsKey(wordPiece)) {
                    allPieces.add(new MaskPiece(pos, hyphenWords.get(wordPiece)));
                }
            }
        }
        return allPieces;
    }

    private final Pattern wordPattern = Pattern.compile("([\\p{javaUpperCase}\\p{javaLowerCase}]+)");

    public String hyphenate(String phrase) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        Matcher m = wordPattern.matcher(phrase);
        while (m.find()) {
            sb.append(phrase.substring(pos, m.start()));
            if (!hyphCache.containsKey(m.group(1))) {
                hyphCache.put(m.group(1), hyphenateWord(m.group(1)));
            }
            sb.append(hyphCache.get(m.group(1)));
            pos = m.end();
        }
        sb.append(phrase.substring(pos));
        return sb.toString();
    }

    private String hyphenateWord(String word) {
        if (word.length() < leftHyphenMin + rightHyphenMin) {
            return word;
        }

        word = "." + word + ".";
        short[] fullmask = new short[word.length() + 1];
        for (MaskPiece mp : findMaskPieces(word.toLowerCase())) {
            mp.merge(fullmask);
        }

        for (int i = 0; i < leftHyphenMin + 1; i++) {
            fullmask[i] = 0;
        }
        for (int i = 0; i < rightHyphenMin + 1; i++) {
            fullmask[fullmask.length - (i + 1)] = 0;
        }
        String hyphenedWord = addHyphens(word, fullmask);
        return hyphenedWord.substring(1, hyphenedWord.length() - 1);
    }

    private String addHyphens(String word, short[] mask) {
        String newWord = "";
        int i = 0;
        for (String ch : word.split("")) {
            if (mask[i] % 2 == 1) {
                newWord += "\u00AD";
            }
            newWord += ch;
            i++;
        }
        return newWord;
    }

    private short[] shortMask(String word, int len) {
        short[] mask = new short[len + 1];
        int i = 0;
        for (String ch : word.split("")) {
            if (ch.matches("[0-9]")) {
                mask[i] = Short.parseShort(ch);
                i--;
            }
            i++;
        }
        return mask;
    }

    private String simpleWord(String word) {
        return word.replaceAll("[0-9]", "");
    }

    private class MaskPiece {
        private final int index;
        private final short[] mask;

        private MaskPiece(int index, short[] mask) {
            this.index = index;
            this.mask = mask;
        }

        private void merge(short[] fullmask) {
            for (int i = 0; i < mask.length; i++) {
                fullmask[index + i] = fullmask[index + i] > mask[i] ? fullmask[index + i] : mask[i];
            }
        }
    }
}
