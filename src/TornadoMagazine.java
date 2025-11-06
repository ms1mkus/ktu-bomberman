class TornadoMagazine extends Magazine {
    private int currentCapacity;
    
    public TornadoMagazine() {
        super("Scary Tornado Magazine", 15, 5000);
        this.currentCapacity = 15;
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

