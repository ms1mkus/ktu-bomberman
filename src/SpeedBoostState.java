public class SpeedBoostState implements PlayerState {
    private long remainingDuration = 10000;
    
    @Override
    public int getMovementSpeedMultiplier() { return 2; }
    
    @Override
    public boolean canPlantBomb() { return false; }
    
    @Override
    public boolean canMove() { return true; }
    
    @Override
    public String getAnimationType() { return "run"; }
    
    @Override
    public PlayerState update(long deltaTime) {
        remainingDuration -= deltaTime;
        if (remainingDuration <= 0) {
            return new NormalState();
        }
        return this;
    }
    
    @Override
    public PlayerState onMovementInput() { return this; }
    
    @Override
    public PlayerState onActionInput() { return this; }
}