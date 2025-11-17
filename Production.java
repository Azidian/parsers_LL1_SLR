import java.util.*;

public class Production {
    private char nonTerminal;
    private List<String> alternatives;

    public Production(char nonTerminal, List<String> alternatives) {
        this.nonTerminal = nonTerminal;
        this.alternatives = alternatives;
    }

    public char getNonTerminal() {
        return nonTerminal;
    }

    public List<String> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        return nonTerminal + " -> " + String.join(" | ", alternatives);
    }
}
