abstract class MapGeneratorTemplate
{

    protected Coordinate[][] map;

    public final void Generate(Coordinate[][] _map)
    {
        map = _map;

        AddBaseTiles();

        AddCentralWalls();
        AddSpawnSurroundings();
        AddFinal();


        AddBorderWalls();
    }

    private void AddBorderWalls()
    {
        // Set the border walls

        for (int j = 1; j < Const.COL - 1; j++)
        {
            map[0][j].img = "wall-center";
            map[Const.LIN - 1][j].img = "wall-center";
        }

        for (int i = 1; i < Const.LIN - 1; i++)
        {
            map[i][0].img = "wall-center";
            map[i][Const.COL - 1].img = "wall-center";
        }

        map[0][0].img = "wall-up-left";
        map[0][Const.COL - 1].img = "wall-up-right";
        map[Const.LIN - 1][0].img = "wall-down-left";
        map[Const.LIN - 1][Const.COL - 1].img = "wall-down-right";
    }

    protected abstract void AddBaseTiles();
    protected abstract void AddCentralWalls();
    protected abstract void AddSpawnSurroundings();
    protected abstract void AddFinal();
}

