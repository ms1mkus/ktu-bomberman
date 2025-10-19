public class BasicPlayer implements PlayerAbilities {

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
}