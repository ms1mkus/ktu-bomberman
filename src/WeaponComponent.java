public abstract class WeaponComponent {
    protected String name;
    
    public WeaponComponent(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public abstract int getPrimaryStat();
    public abstract int getSecondaryStat();
    public abstract String getPrimaryStatLabel();
    public abstract String getSecondaryStatLabel();
    
    public int getCurrentCapacity() {
        return 0;
    }
    
    public int getMaxCapacity() {
        return 0;
    }
    
    public void setCurrentCapacity(int capacity) {
    }
    
    public void reload() {
    }
    
    public int getReloadTime() {
        return 0;
    }
}




