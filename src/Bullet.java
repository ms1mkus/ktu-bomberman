public abstract class Bullet {
    protected int speed;
    protected int damage;
    protected int baseAccuracy;
    protected int recoilPattern[];
    protected int maxSprayDeviation;
    protected int accuracyDecayRate;
    protected int recoveryTime;

    public Bullet(int speed, int damage, int baseAccuracy, int[] recoilPattern, 
                  int maxSprayDeviation, int accuracyDecayRate, int recoveryTime) {
        this.speed = speed;
        this.damage = damage;
        this.baseAccuracy = baseAccuracy;
        this.recoilPattern = recoilPattern.clone();
        this.maxSprayDeviation = maxSprayDeviation;
        this.accuracyDecayRate = accuracyDecayRate;
        this.recoveryTime = recoveryTime;
    }
    
    public int getSpeed() { return speed; }
    public int getDamage() { return damage; }
    public int getBaseAccuracy() { return baseAccuracy; }
    public int getMaxSprayDeviation() { return maxSprayDeviation; }
    public int getAccuracyDecayRate() { return accuracyDecayRate; }
    public int getRecoveryTime() { return recoveryTime; }
    
    public int[] calculateSprayOffset(int shotNumber, long timeSinceLastShot) {
        int patternIndex = shotNumber % recoilPattern.length;
        int baseOffsetX = recoilPattern[patternIndex];
        int baseOffsetY = recoilPattern[(patternIndex + 1) % recoilPattern.length];
        
        int currentAccuracy = calculateCurrentAccuracy(timeSinceLastShot);
        double accuracyMultiplier = currentAccuracy / 100.0;
        int maxDeviation = (int)(maxSprayDeviation * (1.5 - accuracyMultiplier));
        
        int minSpread = maxSprayDeviation / 3;
        int totalMaxSpread = Math.max(minSpread, maxDeviation);
        int sprayX = (int)(Math.random() * totalMaxSpread * 2 - totalMaxSpread);
        int sprayY = (int)(Math.random() * totalMaxSpread * 2 - totalMaxSpread);
        
        int baseRandomX = (int)(Math.random() * (maxSprayDeviation / 2) - (maxSprayDeviation / 4));
        int baseRandomY = (int)(Math.random() * (maxSprayDeviation / 2) - (maxSprayDeviation / 4));
        
        int finalX = baseOffsetX + sprayX + baseRandomX;
        int finalY = baseOffsetY + sprayY + baseRandomY;
        
        return new int[]{finalX, finalY};
    }
    
    private int calculateCurrentAccuracy(long timeSinceLastShot) {
        if (timeSinceLastShot >= recoveryTime) {
            return baseAccuracy;
        }
        
        double recoveryPercent = (double)timeSinceLastShot / recoveryTime;
        int accuracyLoss = (100 - baseAccuracy) * accuracyDecayRate / 100;
        int currentAccuracy = baseAccuracy - accuracyLoss + (int)(accuracyLoss * recoveryPercent);
        
        return Math.max(10, Math.min(baseAccuracy, currentAccuracy));
    }
    
    public abstract String getSpriteType();
    public abstract String getBulletType();
    public abstract int[] getRecoilPattern();
    public abstract int getBulletCount();
    public abstract double calculatePenetration(int blockHealth);
}