class FastWeaponFactory extends AbstractWeaponFactory {
    
    @Override
    public WeaponComponent getBullet() {
        return new FastBullet();
    }
    
    @Override
    public WeaponComponent getMagazine() {
        return new FastMagazine();
    }
    
    @Override
    public WeaponComponent getBarrel() {
        return new FastBarrel();
    }
}

class HeavyWeaponFactory extends AbstractWeaponFactory {
    
    @Override
    public WeaponComponent getBullet() {
        return new HeavyBullet();
    }
    
    @Override
    public WeaponComponent getMagazine() {
        return new HeavyMagazine();
    }
    
    @Override
    public WeaponComponent getBarrel() {
        return new HeavyBarrel();
    }
}

class TornadoWeaponFactory extends AbstractWeaponFactory {
    
    @Override
    public WeaponComponent getBullet() {
        return new TornadoBullet();
    }
    
    @Override
    public WeaponComponent getMagazine() {
        return new TornadoMagazine();
    }
    
    @Override
    public WeaponComponent getBarrel() {
        return new TornadoBarrel();
    }
}