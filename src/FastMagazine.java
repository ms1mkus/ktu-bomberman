class FastMagazine extends Magazine {
    private int currentCapacity;
    
    public FastMagazine() {
        super("Fast Magazine", 30, 2000);
        this.currentCapacity = 30;
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

