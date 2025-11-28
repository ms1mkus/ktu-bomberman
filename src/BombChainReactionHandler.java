public class BombChainReactionHandler implements ExplosionEffectHandler {
    private ExplosionEffectHandler next;
    
    @Override
    public boolean canHandle(int line, int col) {
        return Server.map[line][col].img.contains("bomb-planted");
    }
    
    @Override
    public ExplosionResult handleExplosion(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range) {
        //System.out.println("Bomb chain reaction detected at (" + line + "," + col + ")");
        if (next != null) {
            return next.handleExplosion(line, col, bombOwnerId, bombL, bombC, dirL, dirC, distance, range);
        }
        return ExplosionResult.CONTINUE;
    }
    
    @Override
    public void setNext(ExplosionEffectHandler next) {
        this.next = next;
    }
}