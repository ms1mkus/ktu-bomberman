public class BuildableWall
{
    int c, l;
    int pid;

    BuildableWall(int _c, int _l, int _pid)
    {
        c = _c;
        l = _l;
        pid = _pid;
    }

    void buildWall()
    {
        MapUpdatesThrowerHandler.changeMap("block-"+Sprite.personColors[pid], l, c);
    }

    void removeWall()
    {

        if (MapUpdatesThrowerHandler.isBlockOwnedByPlayer(pid, l, c))
        {
            MapUpdatesThrowerHandler.changeMap("floor-1", l, c);
        }
    }

}
