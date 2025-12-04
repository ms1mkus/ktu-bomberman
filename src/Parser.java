import java.util.List;

class Parser
{
    private final List<Token> tokens;
    private int pos = 0;

    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    Expression parse()
    {
        Token t = peek();

        Expression retExpression = null;

        switch (t.type)
        {
            case KILL: retExpression = kill();
        }

        if (retExpression == null)
            retExpression = nop();

        return retExpression;
    }

    private Expression nop()
    {
        return new NopExpression();
    }

    private Expression kill()
    {
       if (consume(TokenType.KILL) != null)
            return new KillExpression();

       return null;
    }

    private Token consume(TokenType expected)
    {
        Token t = peek();
        if (t.type != expected)
        {
           return null;
        }
        pos++;
        return t;
    }

    private Token peek() {
        return tokens.get(pos);
    }
}