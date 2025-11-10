
class MapGeneratorBarren extends MapGeneratorTemplate
{
    protected void AddBaseTiles()
    {
        for (int i = 0; i < Const.LIN; i++)
            for (int j = 0; j < Const.COL; j++)
                map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "floor-1");
    }
    protected void AddCentralWalls()
    {

    }
    protected void AddSpawnSurroundings()
    {

    }
    protected void AddFinal()
    {

    }
}


