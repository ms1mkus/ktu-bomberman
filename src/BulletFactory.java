public abstract class BulletFactory {
    public abstract Bullet createBullet();
    
    public abstract int getFireRate();
    public abstract int getMaxRange();
    public abstract String getFactoryType();
}