class HeavyMagazine extends Magazine {
    private int currentCapacity;
    
    public HeavyMagazine() {
        super("Heavy Magazine", 8, 10000);
        this.currentCapacity = 8;
    }
    
    @Override
    public int getMaxCapacity() {
        return capacity;
    }
    
    @Override
    public int getCurrentCapacity() {
        return currentCapacity;
    }
    
    @Override
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = Math.max(0, Math.min(capacity, currentCapacity));
    }
    
    @Override
    public void reload() {
        this.currentCapacity = capacity;
    }
}

