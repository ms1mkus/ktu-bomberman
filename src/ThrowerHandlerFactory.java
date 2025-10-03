public class ThrowerHandlerFactory
{
    public static ThrowerHandler makeHandler(ThrowerHandlerType type, int id)
    {

        ThrowerHandler ret;

        switch (type)
        {
            case MAP_UPDATES -> ret = new MapUpdatesThrowerHandler(id);
            case COORDINATES -> ret = new CoordinatesThrowerHandler(id);
            case BULLETS -> ret = new BulletThrowerHandler(id);
            default          -> throw new IllegalArgumentException("Unknown thrower handler type: " + type);
        }

        Thread t = new Thread(ret);
        t.start();
        return ret;
    }
}
