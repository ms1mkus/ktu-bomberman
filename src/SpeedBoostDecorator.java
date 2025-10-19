public class SpeedBoostDecorator extends PlayerDecorator {
    private long endTime;
    private static final long DURATION = 10000; // 10 seconds
    
    public SpeedBoostDecorator(PlayerAbilities wrappee) {
        super(wrappee);
        this.endTime = System.currentTimeMillis() + DURATION;
    }
    
    @Override
    public int getMovementSpeed() {
        if (System.currentTimeMillis() > endTime) {
            return super.getMovementSpeed(); // Expired
        }
        return 2; // Double speed
    }
}