import java.util.ArrayList;
import java.util.List;

class Lexer
{
    private final String input;
    private int pos = 0;

    Lexer(String input) {
        this.input = input;
    }

    List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length())
        {
            char c = input.charAt(pos);

            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            if (Character.isDigit(c)) {
                tokens.add(number());
                continue;
            }

            if (Character.isLetter(c)) {
                tokens.add(identifier());
                continue;
            }
        }

        tokens.add(new Token(TokenType.EOF, "eof"));
        return tokens;
    }

    private Token number()
    {
        int start = pos;
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        return new Token(TokenType.NUMBER, input.substring(start, pos));
    }

    private Token identifier()
    {
        int start = pos;
        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            pos++;
        }
        String id = input.substring(start, pos);
        return switch (id) {
            case "tp" -> new Token(TokenType.TP, id);
            case "kill" -> new Token(TokenType.KILL, id);
            default -> new Token(TokenType.BAD_TOKEN, id);
        };
    }
}