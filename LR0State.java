import java.util.*;

public class LR0State {
    private Set<LR0Item> items;
    private int id;

    public LR0State(int id) {
        this.items = new HashSet<>();
        this.id = id;
    }

    public void addItem(LR0Item item) {
        items.add(item);
    }

    public Set<LR0Item> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR0State state = (LR0State) o;
        return Objects.equals(items, state.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return "State " + id + ": " + items;
    }
}
