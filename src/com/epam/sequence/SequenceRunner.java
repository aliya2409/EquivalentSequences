package com.epam.sequence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceRunner {
    public static final String VALID_SEQUENCE_REGEX = "[01]+";
    public static final String REVERSIBLE_SUBSEQUENCE_MOVING_ONES_TO_THE_LEFT = "1{1}0{1,}1{2,}";
    public static final String SPLITTER_TO_FIND_MAX_ZEROS_TOGETHER = "1{1,}";

    // reg ex components in moveZeroesToLeft() ^^"
    public static final String STARTS_WITH_1_0_ = "1{1}0{";
    public static final String TIMES_OR_MORE_1_0 = ",}1{1}0{";
    public static final String TIMES_1 = "}1{1}";


    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String sequenceOne = reader.readLine();
        String sequenceTwo = reader.readLine();

        try {
            validateSequence(sequenceOne, sequenceTwo);
            if (sequenceOne.length() == sequenceTwo.length()) {
                if (isQuantityOfZerosEqual(sequenceOne, sequenceTwo)) {
                    if (areFirstAndLastCharsEqual(sequenceOne, sequenceTwo)) {
                        modifyTwoSeq(sequenceOne, sequenceTwo);
                    } else {
                        System.out.println("The sequences are NOT equivalent!");
                    }
                } else {
                    System.out.println("The sequences are NOT equivalent! Number of zeroes (ones) must be equal in both sequences.");
                }

            } else {
                System.out.println("The sequences are NOT equivalent! Sequence length doesn't match.");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void validateSequence(String sequenceOne, String sequenceTwo) throws RuntimeException {
        if (!sequenceOne.matches(VALID_SEQUENCE_REGEX) || (!sequenceTwo.matches(VALID_SEQUENCE_REGEX))) {
            throw new RuntimeException("The sequences must consist of 0 and 1 only.");
        }
    }

    public static boolean isQuantityOfZerosEqual(String one, String two) {
        int firstSequenceZeroes = calculateZeros(one);
        int secondSequenceZeroes = calculateZeros(two);
        if (firstSequenceZeroes == secondSequenceZeroes) {
            return true;
        }
        return false;
    }

    public static int calculateZeros(String sequence) {
        char[] chars = sequence.toCharArray();
        int zeros = 0;
        for (char ch : chars) {
            if (ch == '0') {
                zeros++;
            }
        }
        return zeros;
    }

    public static boolean areFirstAndLastCharsEqual(String one, String two) {
        int lastCharIndex = one.length() - 1;
        char[] charsOne = one.toCharArray();
        char[] charsTwo = two.toCharArray();
        if (charsOne[0] == charsTwo[0] && charsOne[lastCharIndex] == charsTwo[lastCharIndex]) {
            return true;
        }
        return false;
    }

    // Modification rule: you can reverse only those subsequences that begin and end with 1
    //Solution idea: to transform both sequences into the general form (ones to the left, zeroes to the right)
    public static void modifyTwoSeq(String seqOne, String seqTwo) {
        seqOne = moveOnesToTheLeft(seqOne);
        String modifiedSeqOne = moveZerosToTheRight(seqOne);
        seqTwo = moveOnesToTheLeft(seqTwo);
        String modifiedSeqTwo = moveZerosToTheRight(seqTwo);
        if (modifiedSeqOne.equals(modifiedSeqTwo)) {
            System.out.println("The sequences are equivalent! They can be transformed into " + modifiedSeqOne);
        } else {
            System.out.println("The sequences are NOT equivalent...");
        }
    }


    private static String moveOnesToTheLeft(String sequence) {
        String seqToChange = sequence;
        StringBuilder sb = new StringBuilder(seqToChange);
        Pattern pattern = Pattern.compile(REVERSIBLE_SUBSEQUENCE_MOVING_ONES_TO_THE_LEFT);
        Matcher matcher = pattern.matcher(sb);
        while (matcher.find()) {
            String subsequence = matcher.group();
            StringBuilder subsequenceSB = new StringBuilder(subsequence);
            subsequenceSB = subsequenceSB.reverse();
            sb.replace(matcher.start(), matcher.end(), subsequenceSB.toString());
            seqToChange = sb.toString();
            seqToChange = moveOnesToTheLeft(seqToChange);
        }
        return seqToChange;
    }

    private static String moveZerosToTheRight(String sequence) {
        String seqToChange = sequence;
        int maxZerosNumberInRaw = countMaxNumberOfZerosTogether(sequence);
        int firstPartZeroesNumber;
        int secondPartZeroesNumber;
        for (int i = 1; i < maxZerosNumberInRaw; i++) {
            firstPartZeroesNumber = i;
            secondPartZeroesNumber = firstPartZeroesNumber + 1;  //to exclude the cases where number of zeros in the first part and the second are the same
            StringBuilder regExSB = new StringBuilder();
            regExSB.append(STARTS_WITH_1_0_);
            regExSB.append(secondPartZeroesNumber);              //here's the first part
            regExSB.append(TIMES_OR_MORE_1_0);
            regExSB.append(firstPartZeroesNumber);
            regExSB.append(TIMES_1);                             //here's the second part
            String regEx = regExSB.toString();                   //final regEx looks like "1{1}0{n}1{1}0{n+1}1{1}"
            seqToChange = findAndReverseRepeatedly(seqToChange, regEx);
        }
        return seqToChange;
    }

    private static String findAndReverseRepeatedly(String seqToChange, String regEx) {
        String resultSeq = seqToChange;
        StringBuilder seqSB = new StringBuilder(seqToChange);
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(seqSB);
        if (matcher.find()) {
            String subSeq = matcher.group();
            StringBuilder subSB = new StringBuilder(subSeq);
            subSB = subSB.reverse();
            seqSB.replace(matcher.start(), matcher.end(), subSB.toString());
            seqToChange = seqSB.toString();
            resultSeq = findAndReverseRepeatedly(seqToChange, regEx);
        }
        return resultSeq;
    }

    private static int countMaxNumberOfZerosTogether(String sequence) {
        int maxZerosNumberInRaw = 0;
        String[] zeroRaws = sequence.split(SPLITTER_TO_FIND_MAX_ZEROS_TOGETHER);
        for (int i = 0; i < zeroRaws.length; i++) {
            char[] zeros = zeroRaws[i].toCharArray();
            int zeroCount = zeros.length;
            if (zeroCount > maxZerosNumberInRaw) {
                maxZerosNumberInRaw = zeroCount;
            }
        }
        return maxZerosNumberInRaw;
    }
}
