//thread that triggers gradual map changes right after a bomb is planted

import java.util.Stack;

class MapUpdatesThrowerHandler implements ThrowerHandler {
   boolean bombPlanted;
   int id, l, c;

   private final Stack<BuildWallCommand> builtWalls = new Stack<>();

   MapUpdatesThrowerHandler(int id) {
      this.id = id;
      this.bombPlanted = false;
   }

   void setBombPlanted(int x, int y) {
      x += Const.WIDTH_SPRITE_PLAYER / 2;
      y += 2 * Const.HEIGHT_SPRITE_PLAYER / 3;

      this.c = x/Const.SIZE_SPRITE_MAP;   
      this.l = y/Const.SIZE_SPRITE_MAP;

      this.bombPlanted = true;
   }

   void setBuildableWall(String dir)
   {
      int x = Server.player[id].x;
      int y = Server.player[id].y;

      int xBody = x + Const.WIDTH_SPRITE_PLAYER/2;
      int yBody = y + 2*Const.HEIGHT_SPRITE_PLAYER/3;

      int tileX = xBody / Const.SIZE_SPRITE_MAP;
      int tileY = yBody / Const.SIZE_SPRITE_MAP;

      switch (dir)
      {
         case "up": tileY  -= 1; break;
         case "left": tileX -= 1; break;
         case "right": tileX += 1; break;
         case "down": tileY  += 1; break;
      }

      if (tileX >= 0 && tileX < Const.COL && tileY >= 0 && tileY < Const.LIN)
      {
         if (Server.map[tileX][tileY].img.equals("floor-1"))
         {
            BuildableWall bw = new BuildableWall(tileX, tileY, id);
            BuildWallCommand bwc = new BuildWallCommand(bw);

            bwc.execute();

            builtWalls.push(bwc);

         }
      }
   }

   void undoBuildableWall()
   {
      if (!builtWalls.empty())
      {
         BuildWallCommand bwc = builtWalls.pop();
         bwc.undo();
      }

   }

   //changes the map on server and client
   static void changeMap(String keyWord, int l, int c) {
      Server.map[l][c].img = keyWord;
      ClientManager.sendToAllClients("-1 mapUpdate " + keyWord + " " + l + " " + c);
   }

   static boolean isBlockOwnedByPlayer(int id, int l, int c)
   {
      String tile = Server.map[l][c].img;
      String[] sp = tile.split("-");

      String col = Sprite.personColors[id];

      if (sp.length > 1)
          return sp[0].equals("block") && sp[1].equals(col);

      return false;
   }

   int getColumnOfMap(int x) {
      return x / Const.SIZE_SPRITE_MAP;
   }
   int getLineOfMap(int y) {
      return y / Const.SIZE_SPRITE_MAP;
   }

   private void spawnRandomPowerUp(int l, int c) {
      RandomGenerator randomGen = RandomGenerator.getInstance();
      // 50% chance something drops
      if (randomGen.checkProbability(0.5)) {
         double random = randomGen.nextDouble();
         // 50% of drops are potions, 50% are standard power-ups
         if (random < 0.5) {
            // Potion branch: split 50/50 between healing and poison
            Potion.Type t = randomGen.nextDouble() < 0.5 ? Potion.Type.HEALING : Potion.Type.POISON;
            PotionManager.spawnPotionOnGround(l, c, t);
         } else {
            // Existing power-ups branch
            double p = randomGen.nextDouble();
            if (p < 0.5) {
               changeMap("powerup-bigbomb", l, c);
            } 
            else if (p < 0.8) {
               changeMap("powerup-speedboost", l, c);
            } 
            else {
               changeMap("powerup-ghost", l, c);
            }
         }
      }
   }

   // checks if the fire hit any standing player (center body coordinate)
   void checkIfExplosionKilledSomeone(int linSprite, int colSprite) {
      int linPlayer, colPlayer, x, y;

      for (int id = 0; id < Const.QTY_PLAYERS; id++)
         if (Server.player[id].alive) {
            x = Server.player[id].x + Const.WIDTH_SPRITE_PLAYER / 2;
            y = Server.player[id].y + 2 * Const.HEIGHT_SPRITE_PLAYER / 3;
   
            colPlayer = getColumnOfMap(x);
            linPlayer = getLineOfMap(y);
   
            if (linSprite == linPlayer && colSprite == colPlayer) {
               Server.player[id].alive = false;
               ClientManager.sendToAllClients(id + " newStatus dead");
            }
         }
   }

   @Override
   public void run() {
      while (true) {
         if (bombPlanted) {
            bombPlanted = false;

            for (String index: Const.indexBombPlanted) {
               changeMap("bomb-planted-" + index, l, c);
               try {
                  Thread.sleep(Const.RATE_BOMB_UPDATE);
               } catch (InterruptedException e) {}
            }
            int range = Server.player[id].getExplosionRange();
            Server.player[id].useBigBombPowerUp();
            
            //explosion effects
            new Thrower("center-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c).start();
            checkIfExplosionKilledSomeone(l, c);

            // DOWN - First block
            if (l+1 < Server.map.length && Server.map[l+1][c].img.equals("floor-1")) {
               new Thrower(range > 1 ? "mid-vert-explosion" : "down-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l+1, c).start();
               checkIfExplosionKilledSomeone(l+1, c);

               // DOWN - Second block (only for BigBomb)
               if (range > 1 && l+2 < Server.map.length && Server.map[l+2][c].img.equals("floor-1")) {
                  new Thrower("down-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l+2, c).start();
                  checkIfExplosionKilledSomeone(l+2, c);
               }
            }
            else if (l+1 < Server.map.length && Server.map[l+1][c].img.contains("block")) {
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l+1, c) {
                  @Override
                  public void run() {
                     super.run();
                     spawnRandomPowerUp(l, c);
                  }
               }.start();
            }

            // RIGHT - First block
            if (c+1 < Server.map[0].length && Server.map[l][c+1].img.equals("floor-1")) {
               new Thrower(range > 1 ? "mid-hori-explosion" : "right-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c+1).start();
               checkIfExplosionKilledSomeone(l, c+1);
                    
               // RIGHT - Second block (only for BigBomb)
               if (range > 1 && c+2 < Server.map[0].length && Server.map[l][c+2].img.equals("floor-1")) {
                  new Thrower("right-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c+2).start();
                  checkIfExplosionKilledSomeone(l, c+2);
               }
            }
            else if (c+1 < Server.map[0].length && Server.map[l][c+1].img.contains("block")) {
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c+1) {
                  @Override
                  public void run() {
                     super.run();
                     spawnRandomPowerUp(l, c);
                  }
               }.start();
            }

            // UP - First block
            if (l-1 >= 0 && Server.map[l-1][c].img.equals("floor-1")) {
               new Thrower(range > 1 ? "mid-vert-explosion" : "up-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l-1, c).start();
               checkIfExplosionKilledSomeone(l-1, c);
                    
               // UP - Second block (only for BigBomb)
               if (range > 1 && l-2 >= 0 && Server.map[l-2][c].img.equals("floor-1")) {
                  new Thrower("up-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l-2, c).start();
                  checkIfExplosionKilledSomeone(l-2, c);
               }
            }
            else if (l-1 >= 0 && Server.map[l-1][c].img.contains("block")) {
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l-1, c) {
                  @Override
                  public void run() {
                     super.run();
                     spawnRandomPowerUp(l, c);
                  }
               }.start();
            }

            // LEFT - First block
            if (c-1 >= 0 && Server.map[l][c-1].img.equals("floor-1")) {
               new Thrower(range > 1 ? "mid-hori-explosion" : "left-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c-1).start();
               checkIfExplosionKilledSomeone(l, c-1);
                    
               // LEFT - Second block (only for BigBomb)
               if (range > 1 && c-2 >= 0 && Server.map[l][c-2].img.equals("floor-1")) {
                  new Thrower("left-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c-2).start();
                  checkIfExplosionKilledSomeone(l, c-2);
               }
            }
            else if (c-1 >= 0 && Server.map[l][c-1].img.contains("block")) {
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c-1) {
                  @Override
                  public void run() {
                     super.run();
                     spawnRandomPowerUp(l, c);
                  }
               }.start();
            }

            Server.player[id].numberOfBombs++; // release bomb
            }
            try {Thread.sleep(0);} catch (InterruptedException e) {}
        }
    }
}