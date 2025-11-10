
class MapGeneratorHard extends MapGeneratorTemplate
{
    protected void AddBaseTiles()
    {
        for (int i = 0; i < Const.LIN; i++)
            for (int j = 0; j < Const.COL; j++)
                map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "block");
    }
    protected void AddCentralWalls()
    {
        // fixed central walls
        for (int i = 2; i < Const.LIN - 2; i++)
            for (int j = 2; j < Const.COL - 2; j++)
                if (i % 2 == 0 && j % 2 == 0)
                    map[i][j].img = "wall-center";
    }
    protected void AddSpawnSurroundings()
    {
        // spawn surroundings
        map[1][1].img = "floor-1";
        map[1][2].img = "floor-1";
        map[2][1].img = "floor-1";
        map[Const.LIN - 2][Const.COL - 2].img = "floor-1";
        map[Const.LIN - 3][Const.COL - 2].img = "floor-1";
        map[Const.LIN - 2][Const.COL - 3].img = "floor-1";
        map[Const.LIN - 2][1].img = "floor-1";
        map[Const.LIN - 3][1].img = "floor-1";
        map[Const.LIN - 2][2].img = "floor-1";
        map[1][Const.COL - 2].img = "floor-1";
        map[2][Const.COL - 2].img = "floor-1";
        map[1][Const.COL - 3].img = "floor-1";
    }
    protected void AddFinal()
    {

    }
}
