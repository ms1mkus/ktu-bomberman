public class SpeedBoostDecorator extends PlayerDecorator {
    private long endTime;
    private final long duration;
    
    public SpeedBoostDecorator(PlayerAbilities wrappee) {
        super(wrappee);
        this.duration = RandomGenerator.getInstance().nextInt(5000, 15000); // 5 to 15 seconds
        this.endTime = System.currentTimeMillis() + duration;
    }
    
    @Override
    public int getMovementSpeed() {
        if (System.currentTimeMillis() > endTime) {
            return super.getMovementSpeed(); // Expired
        }
        return 2; // Double speed
    }
}