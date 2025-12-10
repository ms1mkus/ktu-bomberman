public interface GameVisitor {
    void visit(GameWorld world);
    void visit(MapGroup mapGroup);
    void visit(PlayerGroup playerGroup);
    void visit(TileNode tile);
    void visit(PlayerNode player);
}

// implement splashart only for christmas with visitor pattern, 
// memento implement with saving all states before each explosion, so that it can be returned to the play.