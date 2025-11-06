class FastBullet extends Bullet {
    private static final int[] MINIGUN_PATTERN = {0, 2, 1, 3, 2, 4, 3, 2, 1, 0, -1, -2, -1, 0};
    
    public FastBullet() {
        super("Extremely Fast Bullet", 10, 50, 70);
    }
    
    @Override
    public String getSpriteType() {
        return "minigun";
    }
    
    @Override
    public String getBulletType() {
        return "Fast Bullet";
    }
    
    @Override
    public int[] getRecoilPattern() {
        return MINIGUN_PATTERN.clone();
    }
    
    @Override
    public int getBulletCount() {
        return 1;
    }
    
    @Override
    public double calculatePenetration(int blockHealth) {
        return blockHealth <= 100 ? 0.8 : 0.3;
    }
}

class HeavyBullet extends Bullet {
    private static final int[] SHOTGUN_PATTERN = {0, 8, -5, 12, -8, 15, -12, 18, -15, 10, -3, 7, -10, 5};
    
    public HeavyBullet() {
        super("Insanely Heavy Bullet", 4, 300, 25);
    }
    
    @Override
    public String getSpriteType() {
        return "shotgun";
    }
    
    @Override
    public String getBulletType() {
        return "Heavy Bullet";
    }
    
    @Override
    public int[] getRecoilPattern() {
        return SHOTGUN_PATTERN.clone();
    }
    
    @Override
    public int getBulletCount() {
        return 3;
    }
    
    @Override
    public double calculatePenetration(int blockHealth) {
        return blockHealth <= 200 ? 2 : 0.1;
    }
}

class TornadoBullet extends Bullet {
    private static final int[] TORNADO_PATTERN = {0, 15, -10, 20, -15, 25, -20, 30, -25, 20, -10, 15, -5, 0};
    
    public TornadoBullet() {
        super("Twisting Tornado Bullet", 7, 150, 90);
    }
    
    @Override
    public String getSpriteType() {
        return "tornado";
    }
    
    @Override
    public String getBulletType() {
        return "Tornado Bullet";
    }
    
    @Override
    public int[] getRecoilPattern() {
        return TORNADO_PATTERN.clone();
    }
    
    @Override
    public int getBulletCount() {
        return 1;
    }
    
    @Override
    public double calculatePenetration(int blockHealth) {
        if (blockHealth <= 0) return 0;
        double halfDamage = blockHealth * 0.5;
        return Math.max(halfDamage, 1.0);
    }
}