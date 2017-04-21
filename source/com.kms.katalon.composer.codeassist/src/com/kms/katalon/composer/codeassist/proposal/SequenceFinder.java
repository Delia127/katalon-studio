package com.kms.katalon.composer.codeassist.proposal;

import static com.kms.katalon.constants.GlobalStringConstants.CR_DOUBLE_PRIMES;
import static com.kms.katalon.constants.GlobalStringConstants.CR_PRIME;
import static java.lang.Character.toLowerCase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class SequenceFinder {
    private static final int[] EMPTY_SEQUENCE = new int[0];

    private List<int[]> curSequences = new LinkedList<>();

    private List<int[]> nextSequences = new LinkedList<>();

    private int pCompletion, pToken;

    private String completion, token;

    private boolean searched = false;

    public SequenceFinder(String completion, String token) {
        this.completion = completion;
        reset(token);
    }

    public void changeToken(String token) {
        if (ObjectUtils.equals(this.token, token)) {
            return;
        }
        reset(token);
    }

    private void reset(String token) {
        this.token = token;
        this.nextSequences = new LinkedList<>();
        this.curSequences = new LinkedList<>();
        searched = false;
    }

    public List<int[]> findSequences() {
        try {
            if (searched) {
                return curSequences;
            }
            if (isConstantName(completion)) {
                rewriteCompletion();
            }

            int[] start = EMPTY_SEQUENCE;
            curSequences.add(start);

            for (pToken = 0; pToken < token.length(); pToken++) {
                char t = token.charAt(pToken);
                for (int[] activeSequence : curSequences) {
                    int startIndex = activeSequence.length == 0 ? 0 : activeSequence[activeSequence.length - 1] + 1;

                    for (pCompletion = startIndex; pCompletion < completion.length(); pCompletion++) {
                        char c = completion.charAt(pCompletion);

                        if (isSameIgnoreCase(c, t)) {
                            addNewSubsequenceForNext(activeSequence);
                        }
                    }
                }
                curSequences = nextSequences;
                nextSequences = new LinkedList<>();
            }

            // filter
            for (Iterator< int[]>it = curSequences.iterator(); it.hasNext();) {
                int[] candidate = it.next();
                if (candidate.length < token.length()) {
                    it.remove();
                    continue;
                }
            }

            return curSequences;
        } finally {
            searched = true;
        }
    }

    private void addNewSubsequenceForNext(int[] activeSequence) {
        int[] copy = Arrays.copyOf(activeSequence, activeSequence.length + 1);
        copy[pToken] = pCompletion;
        nextSequences.add(copy);
    }

    private void rewriteCompletion() {
        StringBuilder sb = new StringBuilder();

        boolean toUpperCase = false;
        for (char c : completion.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(toUpperCase ? Character.toUpperCase(c) : Character.toLowerCase(c));
                toUpperCase = false;
            } else {
                sb.append(c);
                toUpperCase = true;
            }
        }
        completion = sb.toString();
    }

    private boolean isConstantName(String completion) {
        for (char c : completion.toCharArray()) {
            if (Character.isLetter(c) && Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameIgnoreCase(char c1, char c2) {
        return toLowerCase(c1) == toLowerCase(c2);
    }

    public static String getToken(String replacedString) {
        if (replacedString == null || replacedString.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String newString = replacedString;
        if (replacedString.startsWith(CR_PRIME) || replacedString.startsWith(CR_DOUBLE_PRIMES)) {
            if (newString.length() == 1) {
                newString = StringUtils.EMPTY;
            } else {
                newString = newString.substring(1, newString.length());
            }
        }

        if (newString.endsWith(CR_PRIME) || newString.endsWith(CR_DOUBLE_PRIMES)) {
            if (newString.length() == 1) {
                newString = StringUtils.EMPTY;
            } else {
                newString = newString.substring(0, newString.length() - 1);
            }
        }
        return newString;
    }
}
