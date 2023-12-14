import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Parser {
  private final List<Token> tokens;
  private int currentPos;
  private final Map<String, Object> variables;

  public Parser(List<Token> tokens, int currentPos, Map<String, Object> variables) {
    this.tokens = tokens;
    this.currentPos = currentPos;
    this.variables = variables;
  }

  public void parseProgram() {
    match("buyin");
    parseBlock();
    match("cashout");
    if (currentPos < tokens.size()) {
      throw new RuntimeException("Unexpected tokens after 'cashout'");
    }
  }

  private void parseBlock() {
    while (currentPos < tokens.size() && !tokens.get(currentPos).getValue().equals("cashout")) {
      parseStatement();
    }
  }

  private void parseLoopBlock() {
    while (!tokens.get(currentPos).getValue().equals("endfor")) {
      parseStatement();
    }
  }

  private void parseIfBlock() {
    while (!tokens.get(currentPos).getValue().equals("endif")) {
      parseStatement();
    }
  }

  private void parseWhileBlock() {
    while (!tokens.get(currentPos).getValue().equals("endwhile")) {
      parseStatement();
    }
  }

  private void parseStatement() {
    switch (tokens.get(currentPos).getValue()) {
      case "place":
        parseAssignment();
        break;
      case "shoot":
        parsePrintStatement();
        break;
      case "if":
        parseIfStatement();
        break;
      case "while":
        parseWhileLoop();
        break;
      case "for":
        parseForLoop();
        break;
      default:
        break;
    }
  }

  private void parseAssignment() {
    match("place");
    String variableName = match("ID").getValue();

    Token currentToken = tokens.get(currentPos);
    if (currentToken.getType().equals("COMPARISON")) {
      match("COMPARISON");
      currentToken = tokens.get(currentPos);
      if (currentToken.getType().equals("STRING")) {
        variables.put(variableName, tokens.get(currentPos).getValue());
        currentPos++;
      } else if (currentToken.getType().equals("BRACKET")) {
        match("[");
        List<Object> arrValues = parseArrValues();
        match("]");
        variables.put(variableName, arrValues);

      } else {
        Object value = parseFactor();
        variables.put(variableName, value);
      }
    } else if (currentToken.getType().equals("BRACKET")) {
      match("[");
      Object index = parseFactor();

      if (tokens.get(currentPos).getValue().equals("+")) {
        currentPos++;
        index = (Integer) index + Integer.parseInt(tokens.get(currentPos).getValue());
        currentPos++;
      }
      match("]");

      match("COMPARISON");

      Object value = parseFactor();
      List<Object> arrValues = (List<Object>) variables.get(variableName);
      arrValues.set((int) index, value);

    } else {
      System.out.println("invalid");
    }
  }

  private Object parseFactor() {
    if (tokens.get(currentPos).getType().equals("NUMBER")) {
      return Integer.parseInt(match("NUMBER").getValue());

    } else if (tokens.get(currentPos).getType().equals("STRING")) {
      return match("STRING").getValue();

    } else if (tokens.get(currentPos).getType().equals("ID")) {
      String variableName = match("ID").getValue();

      if (tokens.get(currentPos).getValue().equals("[")) {
        match("[");
        Object index = parseFactor();
        if (tokens.get(currentPos).getValue().equals("+")) {
          currentPos++;
          index = (Integer) index + Integer.parseInt(tokens.get(currentPos).getValue());
          currentPos++;
        }
        match("]");

        List<Object> arr = (List<Object>) variables.get(variableName);

        return arr.get((int) index);

      } else {
        if (variables.containsKey(variableName)) {
          return variables.get(variableName);

        } else {
          throw new RuntimeException("Variable not defined: " + variableName);
        }
      }

    } else if (tokens.get(currentPos).getType().equals("COMPARISON")) {
      return parseComparison();
    } else {
      return 0;
    }
  }

  private Object parseComparison() {
    Object left = parseFactor();
    String operator = tokens.get(currentPos).getValue();
    currentPos++;
    Object right = parseFactor();

    if (operator.equals("==")) {
      return left.equals(right);
    } else if (operator.equals("<")) {
      return ((Number) left).doubleValue() < ((Number) right).doubleValue();
    } else if (operator.equals(">")) {
      return ((Number) left).doubleValue() > ((Number) right).doubleValue();
    } else {
      throw new RuntimeException("Unsupported comparison operator: " + operator);
    }
  }

  private void parsePrintStatement() {
    match("shoot");
    Token currentToken = tokens.get(currentPos);

    if (currentToken.getType().equals("STRING")) {
      System.out.println(currentToken.getValue());
      parseFactor();
    } else {
      System.out.println(parseFactor());
    }
  }

  private void parseIfStatement() {
    match("if");

    Object left = parseFactor();
    String operator = tokens.get(currentPos).getValue();
    currentPos++;
    Object right = parseFactor();

    boolean result = compEls(left, operator, right);

    if (result) {
      match("then");
      parseIfBlock();

    } else {
      while (!tokens.get(currentPos).getValue().equals("endif")) {
        currentPos++;
      }
    }
    while (!tokens.get(currentPos).getValue().equals("endif")) {
      currentPos++;
    }

    match("endif");
  }

  private boolean compEls(Object left, String operator, Object right) {
    int leftVal = (int) left;
    int rightVal = (int) right;

    if (operator.equals(">")) {
      return leftVal > rightVal;
    }

    return false;
  }

  private void parseWhileLoop() {
    match("while");

    Object left = parseFactor();
    String operator = tokens.get(currentPos).getValue();
    currentPos++;
    Object right = parseFactor();
    boolean result = compEls(left, operator, right);

    if (result) {
      match("do");
      parseWhileBlock();
      match("endwhile");
    } else {
      while (!tokens.get(currentPos).getValue().equals("endwhile")) {
        currentPos++;
      }
    }
  }

  private void parseForLoop() {
    match("for");
    match("place");
    Token idToken = match("ID");
    String variableName = idToken.getValue();
    match("COMPARISON");
    int initialValue = (int) parseFactor();
    match("to");
    int endValue = (int) parseFactor();
    match("do");

    for (int i = initialValue; i <= endValue; i++) {
      Map<String, Object> loopVariables = new HashMap<>(variables);
      loopVariables.put(variableName, i);

      Parser loopParser = new Parser(tokens, currentPos, loopVariables);
      loopParser.parseLoopBlock();

    }

    while (!tokens.get(currentPos).getValue().equals("endfor")) {
      currentPos++;
    }
    match("endfor");

  }

  private List<Object> parseArrValues() {
    List<Object> arrValues = new ArrayList<>();
    while (!tokens.get(currentPos).getValue().equals("]")) {
      arrValues.add(parseFactor());
    }
    return arrValues;
  }

  private Token match(String expectedValue) {
    Token currentToken = tokens.get(currentPos);
    if (currentToken.getValue().equals(expectedValue) || currentToken.getType().equals(expectedValue)) {
      currentPos++;
      return currentToken;
    } else {
      throw new RuntimeException("Unexpected token: " + currentToken);
    }
  }

  public static void main(String[] args) {
    try {
      String inputFilePath = args[0];
      String sourceCode = readFile(inputFilePath);

      Lexer lexer = new Lexer(sourceCode);
      List<Token> tokens = lexer.getTokens();

      /*
       * // This block was to help with trouble shooting
       * // during development, feel fun to leave it in
       * // so hope you don't mind some useless comments :)
       * 
       * System.out.println("Tokens: " + tokens);
       * System.out.println("----------");
       */

      Parser parser = new Parser(tokens, 0, new HashMap<>());
      parser.parseProgram();

      System.exit(0);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String readFile(String filePath) throws IOException {
    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
    }
    return content.toString();
  }
}
