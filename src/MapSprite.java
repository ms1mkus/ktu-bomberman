class MapSprite
{
    private int x, y;
    private MapSpriteType type; // intristic state

    MapSprite(int _x, int _y, MapSpriteType _type)
    {
        x = _x;
        y = _y;
        type = _type;
    }

    public int getX() { return x;}
    public int getY() { return y;}
    public void setSpriteType(MapSpriteType t) { type = t; }
    public MapSpriteType getSpriteType() { return type;}


}
