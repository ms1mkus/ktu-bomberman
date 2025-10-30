public class MapGenStrategyImpossible implements MapGenStrategy {

    @Override
    public void Generate(Coordinate[][] map)
    {
        for (int i = 0; i < Const.LIN; i++)
            for (int j = 0; j < Const.COL; j++)
                map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "block");

        // fixed border walls
        for (int j = 1; j < Const.COL - 1; j++) {
            map[0][j].img = "wall-center";
            map[Const.LIN - 1][j].img = "wall-center";
        }
        for (int i = 1; i < Const.LIN - 1; i++) {
            map[i][0].img = "wall-center";
            map[i][Const.COL - 1].img = "wall-center";
        }
        map[0][0].img = "wall-up-left";
        map[0][Const.COL - 1].img = "wall-up-right";
        map[Const.LIN - 1][0].img = "wall-down-left";
        map[Const.LIN - 1][Const.COL - 1].img = "wall-down-right";

        // fixed central walls
        for (int i = 2; i < Const.LIN - 2; i++)
            for (int j = 2; j < Const.COL - 2; j++)
                if (i % 2 == 0 && j % 2 == 0)
                    map[i][j].img = "wall-center";

        // spawn surroundings
        map[1][1].img = "floor-1";
        map[Const.LIN - 2][Const.COL - 2].img = "floor-1";
        map[Const.LIN - 2][1].img = "floor-1";
        map[1][Const.COL - 2].img = "floor-1";
    }

}
