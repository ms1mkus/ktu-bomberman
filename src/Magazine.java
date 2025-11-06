public abstract class Magazine extends WeaponComponent {
    protected int capacity;
    protected int reloadTime;
    
    public Magazine(String name, int capacity, int reloadTime) {
        super(name);
        this.capacity = capacity;
        this.reloadTime = reloadTime;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    @Override
    public int getReloadTime() {
        return reloadTime;
    }
    
    @Override
    public int getPrimaryStat() {
        return capacity;
    }
    
    @Override
    public int getSecondaryStat() {
        return reloadTime;
    }
    
    @Override
    public String getPrimaryStatLabel() {
        return "Capacity";
    }
    
    @Override
    public String getSecondaryStatLabel() {
        return "Reload Time";
    }
    
    public abstract int getMaxCapacity();
    public abstract int getCurrentCapacity();
    public abstract void reload();
    public abstract void setCurrentCapacity(int capacity);
}

