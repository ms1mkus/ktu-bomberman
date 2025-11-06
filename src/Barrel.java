public abstract class Barrel extends WeaponComponent {
    protected int fireRate;
    protected int accuracyModifier;
    
    public Barrel(String name, int fireRate, int accuracyModifier) {
        super(name);
        this.fireRate = fireRate;
        this.accuracyModifier = accuracyModifier;
    }
    
    public int getFireRate() {
        return fireRate;
    }
    
    public int getAccuracyModifier() {
        return accuracyModifier;
    }
    
    @Override
    public int getPrimaryStat() {
        return fireRate;
    }
    
    @Override
    public int getSecondaryStat() {
        return accuracyModifier;
    }
    
    @Override
    public String getPrimaryStatLabel() {
        return "Fire Rate";
    }
    
    @Override
    public String getSecondaryStatLabel() {
        return "Accuracy Mod";
    }
}




