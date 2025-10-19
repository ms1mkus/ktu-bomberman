class PlayerDecorator implements PlayerAbilities {
    private PlayerAbilities wrappee;
    
    public PlayerDecorator(PlayerAbilities wrappee) {
        this.wrappee = wrappee;
    }
    
    @Override
    public int getExplosionRange() {
       return wrappee.getExplosionRange();
    }

    @Override
    public int getMovementSpeed() {
       return wrappee.getMovementSpeed();
    }

    @Override
    public boolean isGhost() {
        return wrappee.isGhost();
    }
}