class PlayerDecorator implements PlayerAbilities, Cloneable {
    protected PlayerAbilities wrappee;
    
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
    
    @Override
    public PlayerAbilities makeCopy() {
        try {
            PlayerDecorator copy = (PlayerDecorator) this.clone();
            copy.wrappee = this.wrappee.makeCopy();
            return copy;
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
    
    public PlayerAbilities getWrappee() {
        return wrappee;
    }
}