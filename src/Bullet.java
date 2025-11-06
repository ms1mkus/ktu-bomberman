public abstract class Bullet extends WeaponComponent {
    protected int speed;
    protected int damage;
    protected int baseAccuracy;

    public Bullet(String name, int speed, int damage, int baseAccuracy) {
        super(name);
        this.speed = speed;
        this.damage = damage;
        this.baseAccuracy = baseAccuracy;
    }
    
    public int getSpeed() { return speed; }
    public int getDamage() { return damage; }
    public int getBaseAccuracy() { return baseAccuracy; }
    
    @Override
    public int getPrimaryStat() {
        return baseAccuracy;
    }
    
    @Override
    public int getSecondaryStat() {
        return damage;
    }
    
    @Override
    public String getPrimaryStatLabel() {
        return "Accuracy";
    }
    
    @Override
    public String getSecondaryStatLabel() {
        return "Damage";
    }
    
    public int[] calculateSprayOffset(int shotNumber, long timeSinceLastShot) {
        RandomGenerator randomGen = RandomGenerator.getInstance();
        int maxSpread = (100 - baseAccuracy) / 2;
        
        int sprayX = (int)(randomGen.nextDouble() * maxSpread * 2 - maxSpread);
        int sprayY = (int)(randomGen.nextDouble() * maxSpread * 2 - maxSpread);
        
        return new int[]{sprayX, sprayY};
    }
    
    public abstract String getSpriteType();
    public abstract String getBulletType();
    public abstract int[] getRecoilPattern();
    public abstract int getBulletCount();
    public abstract double calculatePenetration(int blockHealth);
}