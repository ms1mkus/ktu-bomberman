
import java.awt.*;

class MapSpriteType
{

    private final String name;
    private final Image image;

    public MapSpriteType(String _name, Image _image)
    {
        name = _name;
        image = _image;
    }

    public String getName() { return name;}
    public Image getImage() { return image;}

}
