class BuildWallCommand implements Command
{

    private final BuildableWall wall;

    BuildWallCommand(BuildableWall _wall)
    {
        this.wall = _wall;
    }

    @Override
    public void execute()
    {
        wall.buildWall();
    }

    @Override
    public void undo()
    {
        wall.removeWall();
    }

}
