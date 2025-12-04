//thread that triggers gradual map changes right after a bomb is planted

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class MapUpdatesThrowerHandler implements ThrowerHandler {
   boolean bombPlanted;
   int id, l, c;

   private static Map<String, Integer> bombOwners = new HashMap<>();
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

      String key = l + "," + c;
      bombOwners.put(key, id);
   }

   static int getBombOwner(int line, int col) {
      String key = line + "," + col;
      return bombOwners.getOrDefault(key, -1);
   }
   
   static void removeBombOwner(int line, int col) {
      String key = line + "," + col;
      bombOwners.remove(key);
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

   private boolean processExplosionEffect(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range) {

      //System.out.println("\nProcessing explosion effect at (" + line + "," + col + ")");
      ExplosionEffectHandler chain = ExplosionChainBuilder.buildExplosionChain();
      ExplosionResult result = processChain(chain, line, col, bombOwnerId, bombL, bombC, dirL, dirC, distance, range);
    
      return result.continuePropagation;
   }

   private ExplosionResult processChain(ExplosionEffectHandler handler, int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range) {
      if (handler == null) {
        return ExplosionResult.CONTINUE; // End of chain - continue through empty space
      }
    
      if (handler.canHandle(line, col)) {
        return handler.handleExplosion(line, col, bombOwnerId, bombL, bombC, dirL, dirC, distance, range);
      } else {
        // This handler can't handle it, try the next one
        return processChain(getNextHandler(handler), line, col, bombOwnerId, bombL, bombC, dirL, dirC, distance, range);
      }
   }

   private ExplosionEffectHandler getNextHandler(ExplosionEffectHandler handler) {
      try {
         java.lang.reflect.Field nextField = handler.getClass().getDeclaredField("next");
         nextField.setAccessible(true);
         return (ExplosionEffectHandler) nextField.get(handler);
      } catch (Exception e) {
         return null;
      }
   }

   private void explodeInDirection(int startL, int startC, int dirL, int dirC, int range, int bombOwnerId) {
    for (int i = 1; i <= range; i++) {
        int currentL = startL + (dirL * i);
        int currentC = startC + (dirC * i);
        
        if (currentL < 0 || currentL >= Server.map.length || 
            currentC < 0 || currentC >= Server.map[0].length) {
            break;
        }
        
        // Use Chain of Responsibility to handle explosion effects with direction context
        boolean continueExplosion = processExplosionEffect(currentL, currentC, bombOwnerId, startL, startC, dirL, dirC, i, range);
        
        if (!continueExplosion) {
            break; // Stop explosion propagation
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
            
            // CHECK: Is this bomb itself covered?
            boolean bombCenterCovered = false;
            for (int i = 0; i < Const.QTY_PLAYERS; i++) {
               if (Server.player[i] != null && Server.player[i].isBombCovered(l, c)) {
                     bombCenterCovered = true;
                     //System.out.println("BOMB CENTER IS COVERED at (" + l + "," + c + ")");
                     break;
               }
            }
            
            if (bombCenterCovered) {
               changeMap("floor-1", l, c);
               Server.player[id].numberOfBombs++;
               continue;
            }
            
            int range = Server.player[id].getExplosionRange();
            Server.player[id].useBigBombPowerUp();

            processExplosionEffect(l, c, id, l, c, 0, 0, 0, range);
            
            explodeInDirection(l, c, 1, 0, range, id);  // DOWN
            explodeInDirection(l, c, -1, 0, range, id); // UP  
            explodeInDirection(l, c, 0, 1, range, id);  // RIGHT
            explodeInDirection(l, c, 0, -1, range, id); // LEFT

            Server.player[id].numberOfBombs++;
         }
         try {Thread.sleep(0);} catch (InterruptedException e) {}
      }
   }
}