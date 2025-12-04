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
            case KILL: retExpression = kill(); break;
            case TP: retExpression = teleport(); break;
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

    private Expression teleport()
    {
        if (consume(TokenType.TP) != null)
        {
            int target;
            int destination;

            Token tt = consume(TokenType.NUMBER);


            if (tt != null)
            {
                target = Integer.parseInt(tt.text);

                Token td = consume(TokenType.NUMBER);

                if (td != null)
                {
                    destination = Integer.parseInt(td.text);

                    return new TeleportExpression(target, destination);
                }
            }
        }

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