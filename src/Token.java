enum TokenType
{
    NONE,

    // identifiers
    TP, KILL,

    NUMBER,
    EOF,
    BAD_TOKEN,
}

class Token
{
    final TokenType type;
    final String text;

    Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }
}