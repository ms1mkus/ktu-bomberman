import java.util.List;

public class ConsoleCommandHelper
{

    public static String Execute(int requestPlayerId, String command)
    {
        Lexer lexer = new Lexer(command);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Expression e = parser.parse();

        String messageBack = e.interpret(requestPlayerId);

        return messageBack;
    }

}
