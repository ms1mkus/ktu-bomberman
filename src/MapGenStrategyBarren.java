
public class MapGenStrategyBarren implements MapGenStrategy {
    @Override
    public void Generate(Coordinate[][] map)
    {

        for (int i = 0; i < Const.LIN; i++)
            for (int j = 0; j < Const.COL; j++)
                map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "floor-1");

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

    }
}
