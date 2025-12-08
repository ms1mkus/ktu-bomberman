public class NormalState implements PlayerState {
    private long idleTimer = 0;
    
    @Override
    public int getMovementSpeedMultiplier() { return 1; }
    
    @Override
    public boolean canPlantBomb() { return true; }
    
    @Override
    public boolean canMove() { return true; }
    
    @Override
    public String getAnimationType() { return "wait"; }
    
    @Override
    public PlayerState update(long deltaTime) {
        idleTimer += deltaTime;
        if (idleTimer >= 5000) {
            return new IdleState();
        }
        return this;
    }
    
    @Override
    public PlayerState onMovementInput() {
        idleTimer = 0;
        return this;
    }
    
    @Override
    public PlayerState onActionInput() {
        return this;
    }
}