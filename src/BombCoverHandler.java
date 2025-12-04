public class BombCoverHandler implements ExplosionEffectHandler {
    private ExplosionEffectHandler next;
    
    @Override
    public boolean canHandle(int line, int col) {
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            if (Server.player[i] != null && 
                Server.player[i].isBombCovered(line, col)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ExplosionResult handleExplosion(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, 
        int distance, int range) {
        MapUpdatesThrowerHandler.changeMap("floor-1", line, col);
        
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            if (Server.player[i] != null) {
                Server.player[i].removeBombCover();
            }
        }
        return ExplosionResult.STOP;
    }
    
    @Override
    public void setNext(ExplosionEffectHandler next) {
        this.next = next;
    }
}