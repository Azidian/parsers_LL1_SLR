import java.util.*;

public class SLR1Parser {
    private Grammar grammar;
    private Grammar augmentedGrammar;
    private FirstFollowCalculator calculator;
    private List<LR0State> states;
    private Map<Integer, Map<Character, Integer>> gotoTable;
    private Map<Integer, Map<Character, String>> actionTable;
    private boolean isSLR1;
    private List<ProductionRule> allProductions;

    private static class ProductionRule {
        char lhs;
        String rhs;
        
        ProductionRule(char lhs, String rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    public SLR1Parser(Grammar grammar, FirstFollowCalculator calculator) {
        this.grammar = grammar;
        this.calculator = calculator;
        this.augmentedGrammar = grammar.augment();
        this.states = new ArrayList<>();
        this.gotoTable = new HashMap<>();
        this.actionTable = new HashMap<>();
        this.allProductions = new ArrayList<>();
        
        // Build a flat list of all productions (each alternative is a separate production)
        for (Production prod : grammar.getProductions()) {
            for (String alt : prod.getAlternatives()) {
                allProductions.add(new ProductionRule(prod.getNonTerminal(), alt));
            }
        }
        
        this.isSLR1 = buildAutomaton();
    }

    private boolean buildAutomaton() {
        LR0State initial = new LR0State(0);
        initial.addItem(new LR0Item('Z', String.valueOf(grammar.getStartSymbol()), 0));
        closure(initial);
        states.add(initial);

        Queue<LR0State> queue = new LinkedList<>();
        queue.add(initial);
        Map<Set<LR0Item>, Integer> stateMap = new HashMap<>();
        stateMap.put(initial.getItems(), 0);

        while (!queue.isEmpty()) {
            LR0State current = queue.poll();
            Map<Character, Set<LR0Item>> transitions = new HashMap<>();

            for (LR0Item item : current.getItems()) {
                Character next = item.getNextSymbol();
                if (next != null) {
                    transitions.putIfAbsent(next, new HashSet<>());
                    transitions.get(next).add(item.advance());
                }
            }

            for (Map.Entry<Character, Set<LR0Item>> entry : transitions.entrySet()) {
                char symbol = entry.getKey();
                LR0State newState = new LR0State(states.size());
                for (LR0Item item : entry.getValue()) {
                    newState.addItem(item);
                }
                closure(newState);

                Integer existingId = stateMap.get(newState.getItems());
                if (existingId == null) {
                    stateMap.put(newState.getItems(), newState.getId());
                    states.add(newState);
                    queue.add(newState);
                    existingId = newState.getId();
                }

                if (Character.isUpperCase(symbol)) {
                    gotoTable.putIfAbsent(current.getId(), new HashMap<>());
                    gotoTable.get(current.getId()).put(symbol, existingId);
                } else {
                    actionTable.putIfAbsent(current.getId(), new HashMap<>());
                    actionTable.get(current.getId()).put(symbol, "s" + existingId);
                }
            }
        }

        return buildActionTable();
    }

    private void closure(LR0State state) {
        Queue<LR0Item> queue = new LinkedList<>(state.getItems());
        Set<LR0Item> processed = new HashSet<>(state.getItems());

        while (!queue.isEmpty()) {
            LR0Item item = queue.poll();
            Character next = item.getNextSymbol();

            if (next != null && Character.isUpperCase(next)) {
                for (String prod : augmentedGrammar.getProductionsFor(next)) {
                    LR0Item newItem = new LR0Item(next, prod, 0);
                    if (processed.add(newItem)) {
                        state.addItem(newItem);
                        queue.add(newItem);
                    }
                }
            }
        }
    }

    private boolean buildActionTable() {
        for (LR0State state : states) {
            actionTable.putIfAbsent(state.getId(), new HashMap<>());

            for (LR0Item item : state.getItems()) {
                if (item.isComplete()) {
                    if (item.getNonTerminal() == 'Z') {
                        if (actionTable.get(state.getId()).containsKey('$')) {
                            return false;
                        }
                        actionTable.get(state.getId()).put('$', "acc");
                    } else {
                        int prodIndex = findProductionIndex(item);
                        if (prodIndex == -1) {
                            continue;
                        }
                        
                        Set<Character> followSet = calculator.getFollowSets().get(item.getNonTerminal());
                        if (followSet == null) {
                            continue;
                        }
                        
                        for (char a : followSet) {
                            if (actionTable.get(state.getId()).containsKey(a)) {
                                return false;
                            }
                            actionTable.get(state.getId()).put(a, "r" + prodIndex);
                        }
                    }
                }
            }
        }

        return true;
    }

    private int findProductionIndex(LR0Item item) {
        for (int i = 0; i < allProductions.size(); i++) {
            ProductionRule rule = allProductions.get(i);
            if (rule.lhs == item.getNonTerminal() && rule.rhs.equals(item.getProduction())) {
                return i;
            }
        }
        return -1;
    }

    public boolean isSLR1() {
        return isSLR1;
    }

    public boolean parse(String input) {
        if (!input.endsWith("$")) {
            input += "$";
        }

        Stack<Integer> stateStack = new Stack<>();
        Stack<Character> symbolStack = new Stack<>();
        stateStack.push(0);
        int pos = 0;

        while (true) {
            int currentState = stateStack.peek();
            char currentChar = input.charAt(pos);

            if (!actionTable.containsKey(currentState) || 
                !actionTable.get(currentState).containsKey(currentChar)) {
                return false;
            }

            String action = actionTable.get(currentState).get(currentChar);

            if (action.equals("acc")) {
                return true;
            } else if (action.startsWith("s")) {
                int nextState = Integer.parseInt(action.substring(1));
                stateStack.push(nextState);
                symbolStack.push(currentChar);
                pos++;
            } else if (action.startsWith("r")) {
                int prodIndex = Integer.parseInt(action.substring(1));
                ProductionRule rule = allProductions.get(prodIndex);
                
                int popCount = rule.rhs.equals("e") ? 0 : rule.rhs.length();
                for (int i = 0; i < popCount; i++) {
                    stateStack.pop();
                    if (!symbolStack.isEmpty()) {
                        symbolStack.pop();
                    }
                }

                int topState = stateStack.peek();
                if (!gotoTable.containsKey(topState) || 
                    !gotoTable.get(topState).containsKey(rule.lhs)) {
                    return false;
                }

                symbolStack.push(rule.lhs);
                stateStack.push(gotoTable.get(topState).get(rule.lhs));
            } else {
                return false;
            }
        }
    }
}