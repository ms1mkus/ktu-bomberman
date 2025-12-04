

interface Expression
{
    String interpret(int playerId);
}

class NopExpression implements Expression
{
    @Override
    public String interpret(int playerId)
    {
       return "Unknown command";
    }
}

class KillExpression implements Expression
{
    @Override
    public String interpret(int playerId)
    {
        Server.player[playerId].alive = false;
        ClientManager.sendToAllClients(playerId + " newStatus dead");

        return "Killing player " + playerId;
    }
}