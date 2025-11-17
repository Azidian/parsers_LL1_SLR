# LL(1) and SLR(1) Grammar Parser

A comprehensive Java implementation of LL(1) and SLR(1) parsers for context-free grammars. This tool analyzes grammars to determine if they are LL(1), SLR(1), 
both, or neither, and provides interactive parsing capabilities.

## Features
- **Dual Parser Support**: Implements both LL(1) and SLR(1) parsing algorithms
- **Automatic Grammar Analysis**: Determines if a grammar is LL(1), SLR(1), both, or neither
- **FIRST and FOLLOW Set Calculation**: Computes FIRST and FOLLOW sets for all non-terminals
- **Interactive Parsing**: Test strings against your grammar in real-time
- **User-Friendly Interface**: Color-coded console output with helpful menus
- **Epsilon Production Support**: Properly handles empty string (ε) productions
- **Comprehensive Examples**: Built-in examples to help you get started

## Requirements
- Java 8 or higher
- No external dependencies required

## Installation

1. Clone this repository:
```bash
git clone https://github.com/yourusername/grammar-parser.git
cd grammar-parser
```

2. Compile the Java files:
```bash
javac *.java
```

3. Run the program:
```bash
java Main
```

## Usage

### Grammar Input Format

- **Non-terminals**: Capital letters (S, A, B, C, etc.)
- **Terminals**: Lowercase letters and symbols (a, b, +, *, (, ), i, etc.)
- **Epsilon**: Use `e` to represent empty string (ε)
- **Start Symbol**: `S` is always the start symbol
- **Production Format**: `<Nonterminal> -> <alternative1> | <alternative2> | ...`
- **Alternative Formats**: 
  - Pipe-separated: `A -> aAd | d`
  - Space-separated: `A -> aAd d`

### Example Sessions

#### Example 1: LL(1) and SLR(1) Grammar

```
Enter the number of nonterminals: 3

Production 1: S -> AB
Production 2: A -> aAd  d
Production 3: B -> bBc  e

Grammar is both LL(1) and SLR(1)!

Test strings:
- d$ → yes
- aaddbbc$ → yes
- a$ → no
```

#### Example 2: SLR(1) Only Grammar

```
Enter the number of nonterminals: 3

Production 1: S -> S+T | T
Production 2: T -> T*F | F
Production 3: F -> (S) | i

Grammar is SLR(1).

Test strings:
- i+i$ → yes
- (i)$ → yes
- (i+i)*i$ → yes
```

#### Example 3: Neither LL(1) nor SLR(1)

```
Enter the number of nonterminals: 2

Production 1: S -> A
Production 2: A -> Ab | b

Grammar is neither LL(1) nor SLR(1).
```

## Project Structure

```
.
├── Main.java                    # Main program with interactive menu
├── Grammar.java                 # Grammar representation and manipulation
├── Production.java              # Production rule representation
├── FirstFollowCalculator.java   # FIRST and FOLLOW set computation
├── LL1Parser.java              # LL(1) parser implementation
├── SLR1Parser.java             # SLR(1) parser implementation
├── LR0Item.java                # LR(0) item for LR parsing
└── LR0State.java               # LR(0) state for automaton construction
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source.

## Authors
Isabella Cadavid Posada
Isabella Ocampo Sanchez
Wendy Vanessa Atehortua Chaverra

## Acknowledgments

- Based on algorithms from "Compilers: Principles, Techniques, and Tools" (Dragon Book)

## Support

For questions or issues, please open an issue on the GitHub repository.
