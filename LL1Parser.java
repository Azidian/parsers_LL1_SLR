import java.util.*;

public class LL1Parser {
    private Grammar grammar;
    private FirstFollowCalculator calculator;
    private Map<Character, Map<Character, String>> parseTable;
    private boolean isLL1;

    public LL1Parser(Grammar grammar, FirstFollowCalculator calculator) {
        this.grammar = grammar;
        this.calculator = calculator;
        this.parseTable = new HashMap<>();
        this.isLL1 = buildParseTable();
    }

    private boolean buildParseTable() {
        for (char nt : grammar.getNonTerminals()) {
            parseTable.put(nt, new HashMap<>());
        }

        for (Production prod : grammar.getProductions()) {
            char A = prod.getNonTerminal();
            for (String alpha : prod.getAlternatives()) {
                Set<Character> firstAlpha = calculator.firstOfString(alpha, true);

                for (char a : firstAlpha) {
                    if (a != 'e') {
                        if (parseTable.get(A).containsKey(a)) {
                            return false;
                        }
                        parseTable.get(A).put(a, alpha);
                    }
                }

                if (firstAlpha.contains('e')) {
                    for (char b : calculator.getFollowSets().get(A)) {
                        if (parseTable.get(A).containsKey(b)) {
                            return false;
                        }
                        parseTable.get(A).put(b, alpha);
                    }
                }
            }
        }

        return true;
    }

    public boolean isLL1() {
        return isLL1;
    }

    public boolean parse(String input) {
        if (!input.endsWith("$")) {
            input += "$";
        }

        Stack<Character> stack = new Stack<>();
        stack.push('$');
        stack.push(grammar.getStartSymbol());

        int pos = 0;

        while (!stack.isEmpty()) {
            char top = stack.pop();
            char current = pos < input.length() ? input.charAt(pos) : '$';

            if (top == '$' && current == '$') {
                return true;
            }

            // *** INICIO DE LA CORRECCIÓN ***
            if (!Character.isUpperCase(top)) {
                // if (top == 'e') { // <-- LÍNEA ANTIGUA (ELIMINADA)
                //     continue;
                // }
                if (top == current) { // <-- LÍNEA NUEVA (SIMPLIFICADA)
                    pos++;
                } else {
                    return false;
                }
            } else {
                if (!parseTable.containsKey(top) || !parseTable.get(top).containsKey(current)) {
                    return false;
                }

                String production = parseTable.get(top).get(current);
                // if (!production.equals("e")) { // <-- LÍNEA ANTIGUA
                if (!production.isEmpty()) { // <-- LÍNEA NUEVA
                    for (int i = production.length() - 1; i >= 0; i--) {
                        stack.push(production.charAt(i));
                    }
                }
            }
            // *** FIN DE LA CORRECCIÓN ***
        }

        return pos == input.length();
    }
}