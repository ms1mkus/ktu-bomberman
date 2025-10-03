class FastBulletFactory extends BulletFactory {
    
    @Override
    public Bullet createBullet() {
        return new FastBullet();
    }
    
    @Override
    public int getFireRate() {
        return 150;
    }
    
    @Override
    public int getMaxRange() {
        return 400;
    }
    
    @Override
    public String getFactoryType() {
        return "Fast Bullet Factory";
    }
}

class HeavyBulletFactory extends BulletFactory {
    
    @Override
    public Bullet createBullet() {
        return new HeavyBullet();
    }
    
    @Override
    public int getFireRate() {
        return 300;
    }
    
    @Override
    public int getMaxRange() {
        return 600;
    }
    
    @Override
    public String getFactoryType() {
        return "Heavy Bullet Factory";
    }
}

class TornadoBulletFactory extends BulletFactory {
    
    @Override
    public Bullet createBullet() {
        return new TornadoBullet();
    }
    
    @Override
    public int getFireRate() {
        return 2000;
    }
    
    @Override
    public int getMaxRange() {
        return 800;
    }
    
    @Override
    public String getFactoryType() {
        return "Tornado Bullet Factory";
    }
}