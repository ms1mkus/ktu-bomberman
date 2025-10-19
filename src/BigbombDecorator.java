class BigBombDecorator extends PlayerDecorator {
    private boolean used = false;
    
    public BigBombDecorator(PlayerAbilities wrappee) {
        super(wrappee);
    }
    
    @Override
    public int getExplosionRange() {
        if (!used) {
            return 2; // Double range for next bomb only
        }
        return super.getExplosionRange(); // Back to normal
    }
    
    public void useBigBomb() {
        used = true;
    }
    
    public boolean isUsed() {
        return used;
    }
}