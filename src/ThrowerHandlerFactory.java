public class ThrowerHandlerFactory
{
    public static ThrowerHandler makeHandler(ThrowerHandlerType type, int id)
    {

        ThrowerHandler ret;

        switch (type)
        {
            case MAP_UPDATES -> ret = new MapUpdatesThrower(id);
            case COORDINATES -> ret = new CoordinatesThrower(id);
            default          -> throw new IllegalArgumentException("Unknown thrower handler type: " + type);
        }

        Thread t = new Thread(ret);
        t.start();
        return ret;
    }
}
