
import java.awt.Image;
import java.util.HashMap;

class MapSpriteFactory
{
    private static final HashMap<String, MapSpriteType> types = new HashMap<>();

    public static MapSpriteType getMapSpriteType(String name)
    {

        MapSpriteType st = types.get(name);
        if (st == null)
        {
            System.out.print("Error! Sprite " + name + " not loaded!\n");
            return null;
        }

        return st;
    }

    public static void addSpriteType(String name, Image image)
    {
        MapSpriteType newType = new MapSpriteType(name, image);
        types.put(name, newType);
    }

}
