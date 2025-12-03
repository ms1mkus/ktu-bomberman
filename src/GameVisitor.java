public interface GameVisitor {
    void visit(GameWorld world);
    void visit(MapGroup mapGroup);
    void visit(PlayerGroup playerGroup);
    void visit(TileNode tile);
    void visit(PlayerNode player);
}
