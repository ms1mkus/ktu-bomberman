public interface PlayerState {
    int getMovementSpeedMultiplier();
    boolean canPlantBomb();
    boolean canMove();
    String getAnimationType();
    
    PlayerState update(long deltaTime);
    PlayerState onMovementInput();
    PlayerState onActionInput();
    
    static PlayerState createNormalState() {
        return new NormalState();
    }

    static PlayerState createTeleportState() {
    return new TeleportState();
}
}