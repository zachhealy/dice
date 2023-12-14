import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class dice {
    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                String inputFilePath = args[0];
                String sourceCode = readFile(inputFilePath);

                Lexer lexer = new Lexer(sourceCode);
                List<Token> tokens = lexer.getTokens();

                Parser parser = new Parser(tokens, 0, new HashMap<>());
                parser.parseProgram();

                System.exit(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Scanner scan = new Scanner(System.in);

            StringBuilder sourceCode = new StringBuilder();
            String line = "";
            System.out.print("> ");
            while (scan.hasNextLine() && !(line = scan.nextLine()).equals("cashout")) {
                System.out.print("> ");
                sourceCode.append(line).append("\n");
            }
            sourceCode.append(line);
            String code = sourceCode.toString();

            Lexer lexer = new Lexer(code);
            Parser parser = new Parser(lexer.getTokens(), 0, new HashMap<>());
            parser.parseProgram();

            scan.close();
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
