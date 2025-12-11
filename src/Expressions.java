

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

    int target;
    boolean self;

    KillExpression()
    {
       target = 0;
       self = true;
    }

    KillExpression(int target)
    {
        this.target = target;
        self = false;
    }

    @Override
    public String interpret(int playerId)
    {

        if (self)
        {
            if (Server.IsValidAndAlivePlayer(playerId))
            {
                Server.player[playerId].alive = false;
                ClientManager.sendToAllClients(playerId + " newStatus dead");
            }

        }
        else
        {
            if (Server.IsValidAndAlivePlayer(target))
            {
                Server.player[target].alive = false;
                ClientManager.sendToAllClients(target + " newStatus dead");
            }

        }



        return "Killing player " + playerId;
    }
}

class TeleportExpression implements Expression
{

    int target;
    int destination;

    public TeleportExpression(int target, int destination)
    {
        this.target = target;
        this.destination = destination;
    }

    @Override
    public String interpret(int playerId)
    {

        if (Server.IsValidAndAlivePlayer(target) && Server.IsValidAndAlivePlayer(destination))
        {

            int newX = Server.player[destination].x;
            int newY = Server.player[destination].y;

            Server.player[target].x = newX;
            Server.player[target].y = newY;

            ClientManager.sendToAllClients(target + " newCoordinate " + newX + " " + newY);

            Server.player[target].setState(new TeleportState(), target);
            ClientManager.sendToAllClients(target + " newCoordinate " + newX + " " + newY);
            return "Teleporting player " + target + " to " + destination;
        }

        return "Teleporting failed";
    }
}