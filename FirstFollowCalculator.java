import java.util.*;

public class FirstFollowCalculator {
    private Grammar grammar;
    private Map<Character, Set<Character>> firstSets;
    private Map<Character, Set<Character>> followSets;

    public FirstFollowCalculator(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
        computeFirst();
        computeFollow();
    }

    private void computeFirst() {
        for (char nt : grammar.getNonTerminals()) {
            firstSets.put(nt, new HashSet<>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Production prod : grammar.getProductions()) {
                char nt = prod.getNonTerminal();
                for (String alt : prod.getAlternatives()) {
                    Set<Character> firstOfAlt = firstOfString(alt);
                    int sizeBefore = firstSets.get(nt).size();
                    firstSets.get(nt).addAll(firstOfAlt);
                    if (firstSets.get(nt).size() > sizeBefore) {
                        changed = true;
                    }
                }
            }
        }
    }

    private Set<Character> firstOfString(String str) {
        Set<Character> result = new HashSet<>();
        // *** INICIO DE LA CORRECCIÓN ***
        // if (str.isEmpty() || str.equals("e")) { // <-- LÍNEA ANTIGUA
        if (str.isEmpty()) { // <-- LÍNEA NUEVA
        // *** FIN DE LA CORRECCIÓN ***
            result.add('e');
            return result;
        }

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isUpperCase(c)) {
                result.add(c);
                return result;
            }

            Set<Character> firstOfC = firstSets.get(c);
            if (firstOfC == null) {
                return result;
            }

            boolean hasEpsilon = firstOfC.contains('e');
            for (char f : firstOfC) {
                if (f != 'e') {
                    result.add(f);
                }
            }

            if (!hasEpsilon) {
                return result;
            }

            if (i == str.length() - 1) {
                result.add('e');
            }
        }

        return result;
    }

    private void computeFollow() {
        for (char nt : grammar.getNonTerminals()) {
            followSets.put(nt, new HashSet<>());
        }
        followSets.get(grammar.getStartSymbol()).add('$');

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Production prod : grammar.getProductions()) {
                char A = prod.getNonTerminal();
                for (String alt : prod.getAlternatives()) {
                    for (int i = 0; i < alt.length(); i++) {
                        char B = alt.charAt(i);
                        if (!Character.isUpperCase(B)) continue;

                        String beta = alt.substring(i + 1);
                        Set<Character> firstBeta = firstOfString(beta);

                        int sizeBefore = followSets.get(B).size();
                        for (char f : firstBeta) {
                            if (f != 'e') {
                                followSets.get(B).add(f);
                            }
                        }

                        if (firstBeta.contains('e') || beta.isEmpty()) {
                            followSets.get(B).addAll(followSets.get(A));
                        }

                        if (followSets.get(B).size() > sizeBefore) {
                            changed = true;
                        }
                    }
                }
            }
        }
    }

    public Map<Character, Set<Character>> getFirstSets() {
        return firstSets;
    }

    public Map<Character, Set<Character>> getFollowSets() {
        return followSets;
    }

    public Set<Character> firstOfString(String str, boolean useCache) {
        return firstOfString(str);
    }
}