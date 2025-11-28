public interface ExplosionEffectHandler {
    boolean canHandle(int line, int col);
    ExplosionResult handleExplosion(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range);
    void setNext(ExplosionEffectHandler next);
}

class ExplosionResult {
    public final boolean continuePropagation;
    public final boolean createVisualEffect;
    
    public ExplosionResult(boolean continuePropagation, boolean createVisualEffect) {
        this.continuePropagation = continuePropagation;
        this.createVisualEffect = createVisualEffect;
    }
    
    public static final ExplosionResult STOP = new ExplosionResult(false, false);
    public static final ExplosionResult CONTINUE = new ExplosionResult(true, true);
    public static final ExplosionResult CONTINUE_NO_VISUAL = new ExplosionResult(true, false);
}