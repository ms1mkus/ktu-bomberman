public class TeleportState implements PlayerState {
    private long teleportTime;
    private static final int FROZEN_TIME = 5000;
    
    public TeleportState() {
        this.teleportTime = System.currentTimeMillis();
    }
    
    @Override
    public int getMovementSpeedMultiplier() {
        return 0;
    }
    
    @Override
    public boolean canPlantBomb() {
        return false;
    }
    
    @Override
    public boolean canMove() {
        return false;
    }
    
    @Override
    public String getAnimationType() {
        return "wait";
    }
    
    @Override
    public PlayerState update(long deltaTime) {
        if (System.currentTimeMillis() - teleportTime >= FROZEN_TIME) {
            return new NormalState();
        }
        return this;
    }
    
    @Override
    public PlayerState onMovementInput() {
        return this;
    }
    
    @Override
    public PlayerState onActionInput() {
        return this;
    }
}