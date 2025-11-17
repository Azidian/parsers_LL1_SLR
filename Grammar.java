import java.util.*;

public class Grammar {
    private List<Production> productions;
    private Set<Character> nonTerminals;
    private Set<Character> terminals;
    private char startSymbol;

    public Grammar() {
        this.productions = new ArrayList<>();
        this.nonTerminals = new HashSet<>();
        this.terminals = new HashSet<>();
        this.startSymbol = 'S';
    }

    public void addProduction(Production production) {
        productions.add(production);
        nonTerminals.add(production.getNonTerminal());
        
        for (String alt : production.getAlternatives()) {
            for (char c : alt.toCharArray()) {
                if (!Character.isUpperCase(c) && c != 'e') {
                    terminals.add(c);
                }
            }
        }
    }

    public List<Production> getProductions() {
        return productions;
    }

    public Set<Character> getNonTerminals() {
        return nonTerminals;
    }

    public Set<Character> getTerminals() {
        return terminals;
    }

    public char getStartSymbol() {
        return startSymbol;
    }

    public List<String> getProductionsFor(char nonTerminal) {
        for (Production p : productions) {
            if (p.getNonTerminal() == nonTerminal) {
                return p.getAlternatives();
            }
        }
        return new ArrayList<>();
    }

    public Grammar augment() {
        Grammar augmented = new Grammar();
        augmented.startSymbol = 'Z';
        augmented.addProduction(new Production('Z', Arrays.asList(String.valueOf(startSymbol))));
        
        for (Production p : productions) {
            augmented.addProduction(p);
        }
        
        return augmented;
    }
}
