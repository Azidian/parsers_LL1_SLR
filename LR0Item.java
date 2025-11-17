import java.util.Objects;

public class LR0Item {
    private char nonTerminal;
    private String production;
    private int dotPosition;

    public LR0Item(char nonTerminal, String production, int dotPosition) {
        this.nonTerminal = nonTerminal;
        this.production = production;
        this.dotPosition = dotPosition;
    }

    public char getNonTerminal() {
        return nonTerminal;
    }

    public String getProduction() {
        return production;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    public boolean isComplete() {
        // *** INICIO DE LA CORRECCIÓN ***
        // return dotPosition >= production.length() || 
        //       (dotPosition == 1 && production.equals("e")); // <-- LÍNEA ANTIGUA
        return dotPosition >= production.length(); // <-- LÍNEA NUEVA
        // *** FIN DE LA CORRECCIÓN ***
    }

    public Character getNextSymbol() {
        // *** INICIO DE LA CORRECCIÓN ***
        // if (production.equals("e")) return null; // <-- LÍNEA ANTIGUA (ELIMINADA)
        if (dotPosition < production.length()) {
            return production.charAt(dotPosition);
        }
        return null;
        // *** FIN DE LA CORRECCIÓN ***
    }

    public LR0Item advance() {
        return new LR0Item(nonTerminal, production, dotPosition + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR0Item item = (LR0Item) o;
        return nonTerminal == item.nonTerminal &&
               dotPosition == item.dotPosition &&
               Objects.equals(production, item.production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonTerminal, production, dotPosition);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nonTerminal).append(" -> ");

        // *** INICIO DE LA CORRECCIÓN ***
        // CORRECCIÓN en toString para manejar bien "" (producción vacía)
        if (production.isEmpty()) {
            sb.append("."); // Muestra "B -> ."
            return sb.toString();
        }
        // *** FIN DE LA CORRECCIÓN ***
        
        for (int i = 0; i < production.length(); i++) {
            if (i == dotPosition) sb.append(".");
            sb.append(production.charAt(i));
        }
        if (dotPosition >= production.length()) sb.append(".");
        return sb.toString();
    }
}