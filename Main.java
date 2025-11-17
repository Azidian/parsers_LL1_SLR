import java.util.*;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        showMainMenu(scanner);
        
        System.out.print("Enter the number of nonterminals: ");
        int n = Integer.parseInt(scanner.nextLine().trim());
        Grammar grammar = new Grammar();

        System.out.println("\nEnter " + n + " production rules (format: <Nonterminal> -> <alternatives separated by |>):");
        System.out.println("Example: S -> AB");
        System.out.println("Example: A -> aAd | d");
        System.out.println("Example: B -> bBc | e");
        System.out.println();
        
        for (int i = 0; i < n; i++) {
            System.out.print("Production " + (i + 1) + ": ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split(" -> ");
            char nonTerminal = parts[0].charAt(0);
            
            // Split by | to get alternatives, but also handle space-separated alternatives
            String[] alternatives;
            if (parts[1].contains("|")) {
                // If there's a pipe, split by pipe
                alternatives = parts[1].split("\\|");
            } else {
                // If no pipe, check if there are multiple space-separated tokens
                String[] tokens = parts[1].trim().split("\\s+");
                if (tokens.length > 1) {
                    // Multiple alternatives separated by spaces
                    alternatives = tokens;
                } else {
                    // Single alternative
                    alternatives = new String[]{parts[1].trim()};
                }
            }

            // *** INICIO DE LA CORRECCIÓN ***
            List<String> processedAlts = new ArrayList<>();
            for (String alt : alternatives) {
                // Limpia espacios (importante para entradas como "a | e")
                String processed = alt.trim().replace(" ", ""); 
                
                if (processed.equals("e")) {
                    processedAlts.add(""); // <-- CAMBIO: Usar "" para épsilon
                } else {
                    processedAlts.add(processed);
                }
            }
            
            grammar.addProduction(new Production(nonTerminal, processedAlts));
            // *** FIN DE LA CORRECCIÓN ***
        }

        System.out.println("\n" + CYAN + "=".repeat(50));
        System.out.println("Analyzing grammar...");
        System.out.println("=".repeat(50) + RESET + "\n");

        FirstFollowCalculator calculator = new FirstFollowCalculator(grammar);
        LL1Parser ll1Parser = new LL1Parser(grammar, calculator);
        SLR1Parser slr1Parser = new SLR1Parser(grammar, calculator);

        boolean isLL1 = ll1Parser.isLL1();
        boolean isSLR1 = slr1Parser.isSLR1();

        if (isLL1 && isSLR1) {
            System.out.println("Grammar is both LL(1) and SLR(1)!");
            System.out.println();
            
            while (true) {
                System.out.println("Select a parser (T: for LL(1), B: for SLR(1), Q: quit):");
                String choice = scanner.nextLine().trim();

                if (choice.equals("Q")) {
                    System.out.println("Exiting program. Goodbye!");
                    break;
                }

                if (choice.equals("T") || choice.equals("B")) {
                    String parserType = choice.equals("T") ? "LL(1)" : "SLR(1)";
                    System.out.println("\nUsing " + parserType + " parser.");
                    System.out.println("Enter strings to parse (one per line, empty line to return to menu):");
                    System.out.println("Remember: strings must end with '$'");
                    System.out.println();
                    
                    while (true) {
                        System.out.print("String: ");
                        String input = scanner.nextLine();
                        if (input.isEmpty()) {
                            System.out.println();
                            break;
                        }

                        boolean result;
                        if (choice.equals("T")) {
                            result = ll1Parser.parse(input);
                        } else {
                            result = slr1Parser.parse(input);
                        }

                        System.out.println(result ? "yes" : "no");
                    }
                }
            }
        } else if (isLL1) {
            System.out.println("Grammar is LL(1).");
            System.out.println("\nEnter strings to parse (one per line, empty line to stop):");
            System.out.println("Remember: strings must end with '$'");
            System.out.println();
            
            while (true) {
                System.out.print("String: ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    break;
                }
                boolean result = ll1Parser.parse(input);
                System.out.println(result ? "yes" : "no");
            }
        } else if (isSLR1) {
            System.out.println("Grammar is SLR(1).");
            System.out.println("\nEnter strings to parse (one per line, empty line to stop):");
            System.out.println("Remember: strings must end with '$'");
            System.out.println();
            
            while (true) {
                System.out.print("String: ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    break;
                }
                boolean result = slr1Parser.parse(input);
                System.out.println(result ? "yes" : "no");
            }
        } else {
            System.out.println("Grammar is neither LL(1) nor SLR(1).");
        }

        scanner.close();
    }
    
    private static void showMainMenu(Scanner scanner) {
        while (true) {
            System.out.println(PURPLE + "=".repeat(70));
            System.out.println("        LL(1) AND SLR(1) PARSER - GRAMMAR ANALYZER");
            System.out.println("=".repeat(70) + RESET);
            System.out.println();
            System.out.println(YELLOW + "MAIN MENU:" + RESET);
            System.out.println(YELLOW + "---------" + RESET);
            System.out.println("1. INSTRUCTIONS");
            System.out.println("2. RULES");
            System.out.println("3. EXAMPLES");
            System.out.println("4. START TESTING");
            System.out.println();
            System.out.print("Select an option (1-4): ");
            
            String option = scanner.nextLine().trim();
            System.out.println();
            
            switch (option) {
                case "1":
                    showInstructions();
                    break;
                case "2":
                    showRules();
                    break;
                case "3":
                    showExamples();
                    break;
                case "4":
                    System.out.println(GREEN + "=".repeat(70));
                    System.out.println("STARTING GRAMMAR ANALYSIS");
                    System.out.println("=".repeat(70) + RESET);
                    System.out.println();
                    return;
                default:
                    System.out.println(RED + "Invalid option. Please select 1-4." + RESET);
                    System.out.println();
            }
        }
    }
    
    private static void showInstructions() {
        System.out.println(BLUE + "=".repeat(70));
        System.out.println("INSTRUCTIONS");
        System.out.println("=".repeat(70) + RESET);
        System.out.println();
        System.out.println("1. Enter the number of nonterminals in your grammar");
        System.out.println("2. Enter each production rule in one of these formats:");
        System.out.println("   " + BLUE + "<Nonterminal> -> <alternative1> | <alternative2> ..." + RESET);
        System.out.println("   " + BLUE + "or space-separated: <Nonterminal> -> <alt1> <alt2> ..." + RESET);
        System.out.println("3. The program will analyze if your grammar is LL(1), SLR(1), both, or neither");
        System.out.println("4. Then you can test strings to see if they are accepted by the grammar");
        System.out.println();
        System.out.println(BLUE + "=".repeat(70) + RESET);
        System.out.println();
        System.out.print("Press Enter to return to menu...");
        new Scanner(System.in).nextLine();
        System.out.println();
    }
    
    private static void showRules() {
        System.out.println(GREEN + "=".repeat(70));
        System.out.println("GRAMMAR RULES");
        System.out.println("=".repeat(70) + RESET);
        System.out.println();
        System.out.println("✓ 'S' is always the initial symbol");
        System.out.println("✓ Nonterminals are CAPITAL LETTERS (A, B, C, S, T, F, etc.)");
        System.out.println("✓ Terminals are NOT capital letters (a, b, c, +, *, (, ), i, etc.)");
        System.out.println("✓ Use 'e' to represent epsilon (empty string)");
        System.out.println("✓ All test strings must end with '$'");
        System.out.println("✓ The symbol '$' marks the end of input");
        System.out.println("✓ Alternatives can be separated by '|' or spaces");
        System.out.println();
        System.out.println(GREEN + "=".repeat(70) + RESET);
        System.out.println();
        System.out.print("Press Enter to return to menu...");
        new Scanner(System.in).nextLine();
        System.out.println();
    }
    
    private static void showExamples() {
        System.out.println(YELLOW + "=".repeat(70));
        System.out.println("EXAMPLES");
        System.out.println("=".repeat(70) + RESET);
        System.out.println();
        System.out.println(CYAN + "EXAMPLE 1: LL(1) and SLR(1) Grammar" + RESET);
        System.out.println("----------");
        System.out.println("Number of nonterminals: 3");
        System.out.println("S -> AB");
        System.out.println("A -> aAd | d");
        System.out.println("B -> bBc | e");
        System.out.println();
        System.out.println("Test strings: d$, aaddbbc$, a$");
        System.out.println("Expected results: yes, yes, no");
        System.out.println();
        System.out.println(CYAN + "EXAMPLE 2: SLR(1) Grammar" + RESET);
        System.out.println("----------");
        System.out.println("Number of nonterminals: 3");
        System.out.println("S -> S+T | T");
        System.out.println("T -> T*F | F");
        System.out.println("F -> (S) | i");
        System.out.println();
        System.out.println("Test strings: i+i$, (i)$, (i+i)*i$");
        System.out.println("Expected results: yes, yes, yes");
        System.out.println();
        System.out.println(CYAN + "EXAMPLE 3: Neither LL(1) nor SLR(1)" + RESET);
        System.out.println("----------");
        System.out.println("Number of nonterminals: 2");
        System.out.println("S -> A");
        System.out.println("A -> Ab | b");
        System.out.println();
        System.out.println("This grammar has left recursion and cannot be parsed by LL(1).");
        System.out.println();
        System.out.println(YELLOW + "=".repeat(70) + RESET);
        System.out.println();
        System.out.print("Press Enter to return to menu...");
        new Scanner(System.in).nextLine();
        System.out.println();
    }
}