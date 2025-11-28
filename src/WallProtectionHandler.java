public class WallProtectionHandler implements ExplosionEffectHandler {
    private ExplosionEffectHandler next;
    
    @Override
    public boolean canHandle(int line, int col) {
        return Server.map[line][col].img.contains("wall");
    }
    
@Override
public ExplosionResult handleExplosion(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range) {
    //System.out.println("Explosion stopped by wall at (" + line + "," + col + ")");
    return ExplosionResult.STOP;
}
    
    @Override
    public void setNext(ExplosionEffectHandler next) {
        this.next = next;
    }
}