public interface PlayerAbilities {
   int getExplosionRange();
   int getMovementSpeed();
   boolean isGhost();
   
   PlayerAbilities makeCopy();
}