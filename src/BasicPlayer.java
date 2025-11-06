public class BasicPlayer implements PlayerAbilities, Cloneable {

    @Override
    public int getExplosionRange() { 
        return 1; 
    }

    @Override
    public int getMovementSpeed() { 
        return 1;
    }

    @Override
    public boolean isGhost() {
        return false;
    }
    
    @Override
    public PlayerAbilities makeCopy() {
        try {
            return (PlayerAbilities) this.clone();
        } catch (CloneNotSupportedException e) {
            return new BasicPlayer();
        }
    }
}