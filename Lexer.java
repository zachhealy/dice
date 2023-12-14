import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
  private final List<Token> tokens;
  private final String input;

  public Lexer(String input) {
    this.input = input;
    this.tokens = new ArrayList<>();
    tokenize();
  }

  public List<Token> getTokens() {
    return tokens;
  }

  private void tokenize() {
    Pattern keywordPattern = Pattern.compile(
        "(buyin|cashout|place|sitdown|standup|return|if|then|else|endif|while|do|endwhile|for|to|do|endfor|shoot)");
    Pattern idPattern = Pattern.compile("[a-zA-Z]+");
    Pattern numberPattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
    Pattern stringPattern = Pattern.compile("\"[^\"]*\"");
    Pattern whitespacePattern = Pattern.compile("[ \\t\\n\\r]+");
    Pattern bracketPattern = Pattern.compile("[\\[\\]]");
    Pattern comparisonPattern = Pattern.compile("[\\=\\>\\<]");
    Pattern additionPattern = Pattern.compile("[\\+]");

    Matcher matcher = Pattern.compile(
        String.join("|", "(" + keywordPattern.pattern() + ")", "(" + idPattern.pattern() + ")",
            "(" + numberPattern.pattern() + ")", "(" + stringPattern.pattern() + ")",
            "(" + whitespacePattern.pattern() + ")", "(" + bracketPattern.pattern() + ")",
            "(" + comparisonPattern.pattern() + ")", "(" + additionPattern.pattern() + ")"))
        .matcher(input);

    while (matcher.find()) {
      String token = matcher.group().trim();
      if (!token.isEmpty()) {
        tokens.add(new Token(determineTokenType(token), token));
      }
    }
  }

  private String determineTokenType(String token) {
    if (token.matches(
        "(buyin|cashout|place|sitdown|standup|return|if|then|else|endif|while|do|endwhile|for|to|do|endfor|shoot)")) {
      return "KEYWORD";
    } else if (token.matches("[a-zA-Z]+")) {
      return "ID";
    } else if (token.matches("[0-9]+(\\.[0-9]+)?")) {
      return "NUMBER";
    } else if (token.matches("\"[^\"]*\"")) {
      return "STRING";
    } else if (token.matches("[ \\t\\n\\r]+")) {
      return "WHITESPACE";
    } else if (token.matches("[\\[\\]]")) {
      return "BRACKET";
    } else if (token.matches("[\\=\\>\\<]")) {
      return "COMPARISON";
    } else if (token.matches("[\\+]")) {
      return "OPERATOR";
    } else {
      return "UNKNOWN";
    }
  }
}