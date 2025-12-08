public class IdleState implements PlayerState {
    @Override
    public int getMovementSpeedMultiplier() { return 0; }
    
    @Override
    public boolean canPlantBomb() { return false; }
    
    @Override
    public boolean canMove() { return false; }
    
    @Override
    public String getAnimationType() { return "wait"; }
    
    @Override
    public PlayerState update(long deltaTime) { return this; }
    
    @Override
    public PlayerState onMovementInput() { return new NormalState(); }
    
    @Override
    public PlayerState onActionInput() { return new NormalState(); }
}