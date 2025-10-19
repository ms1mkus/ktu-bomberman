import java.awt.event.KeyEvent;

//thread that sends the next coordinates to clients while W/A/S/D is held down
class CoordinatesThrowerHandler implements ThrowerHandler
{
   boolean up, right, left, down;
   int id;

   CoordinatesThrowerHandler(int id) {
      this.id = id;
      up = down = right = left = false;
   }

   @Override
   public void run() {
      int newX = Server.player[id].x;
      int newY = Server.player[id].y;
      
      while (true) {
         if (up || down || right || left) {
            int speedMultiplier = Server.player[id].getMovementSpeed();
            int moveDistance = Const.RESIZE * speedMultiplier;
            
            if (up)           newY = Server.player[id].y - moveDistance;
            else if (down)    newY = Server.player[id].y + moveDistance;
            else if (right)   newX = Server.player[id].x + moveDistance;
            else if (left)    newX = Server.player[id].x - moveDistance;

            if (coordinateIsValid(newX, newY)) {
               
               Server.player[id].x = newX;
               Server.player[id].y = newY;

               checkPowerUpCollection(newX, newY);
               ClientManager.sendToAllClients(id + " newCoordinate " + newX + " " + newY);

            } else {
               newX = Server.player[id].x;
               newY = Server.player[id].y;
            }
            try {
               Thread.sleep(Const.RATE_COORDINATES_UPDATE / speedMultiplier);
            } catch (InterruptedException e) {}
         }
         try {Thread.sleep(0);} catch (InterruptedException e) {}
      }
   }

   private void checkPowerUpCollection(int playerX, int playerY) {
      int startCol = getColumnOfMap(playerX + Const.VAR_X_SPRITES);
      int startLine = getLineOfMap(playerY + Const.VAR_Y_SPRITES);
      int endCol = getColumnOfMap(playerX + Const.VAR_X_SPRITES + Const.SIZE_SPRITE_MAP - 1);
      int endLine = getLineOfMap(playerY + Const.VAR_Y_SPRITES + Const.SIZE_SPRITE_MAP - 1);
   
      // Check all tiles the player is on
      for (int line = startLine; line <= endLine; line++) {
         for (int col = startCol; col <= endCol; col++) {
            if (Server.map[line][col].img.equals("powerup-bigbomb")) {                
               Server.player[id].addBigBomb();
               MapUpdatesThrowerHandler.changeMap("floor-1", line, col);
               ClientManager.sendToAllClients(id + " powerUp bigbomb");
               return;
            }
            else if (Server.map[line][col].img.equals("powerup-speedboost")) {
               Server.player[id].addSpeedBoost();
               MapUpdatesThrowerHandler.changeMap("floor-1", line, col);
               ClientManager.sendToAllClients(id + " powerUp speedboost");
               return;
            }
            else if (Server.map[line][col].img.equals("powerup-ghost")) {
               Server.player[id].addGhost();
               MapUpdatesThrowerHandler.changeMap("floor-1", line, col);
               ClientManager.sendToAllClients(id + " powerUp ghost");
               return;
            }
         }
      }
   }

   int getColumnOfMap(int x) {
      return x/Const.SIZE_SPRITE_MAP;
   }
   int getLineOfMap(int y) {
      return y/Const.SIZE_SPRITE_MAP;
   }

   // finds which map sprites the player is on and checks if they are valid
   boolean coordinateIsValid(int newX, int newY) {
      if (!Server.player[id].alive)
         return false;

   //checks if the player went into the fire (center body coordinate)
      int xBody = newX + Const.WIDTH_SPRITE_PLAYER/2;
      int yBody = newY + 2*Const.HEIGHT_SPRITE_PLAYER/3;

      if (Server.map[getLineOfMap(yBody)][getColumnOfMap(xBody)].img.contains("explosion")) {
         Server.player[id].alive = false;
         ClientManager.sendToAllClients(id + " newStatus dead");
         return true;
      }
      
      int x[] = new int[4], y[] = new int[4];
      int c[] = new int[4], l[] = new int[4];


   // RELATIVE TO THE NEW COORDINATE

   // 0: top left corner point
      x[0] = Const.VAR_X_SPRITES + newX + Const.RESIZE;
      y[0] = Const.VAR_Y_SPRITES + newY + Const.RESIZE;
   // 1: top right corner point
      x[1] = Const.VAR_X_SPRITES + newX + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[1] = Const.VAR_Y_SPRITES + newY + Const.RESIZE;
   // 2: bottom left corner point
      x[2] = Const.VAR_X_SPRITES + newX + Const.RESIZE;
      y[2] = Const.VAR_Y_SPRITES + newY + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
   // 3: bottom right corner point
      x[3] = Const.VAR_X_SPRITES + newX + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[3] = Const.VAR_Y_SPRITES + newY + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      
      for (int i = 0; i < 4; i++) { 
         c[i] = getColumnOfMap(x[i]);
         l[i] = getLineOfMap(y[i]);
      }

      if (
         (Server.map[l[0]][c[0]].img.equals("floor-1") || Server.map[l[0]][c[0]].img.contains("explosion") || Server.map[l[0]][c[0]].img.startsWith("powerup-")) && 
         (Server.map[l[1]][c[1]].img.equals("floor-1") || Server.map[l[1]][c[1]].img.contains("explosion") || Server.map[l[0]][c[0]].img.startsWith("powerup-")) &&
         (Server.map[l[2]][c[2]].img.equals("floor-1") || Server.map[l[2]][c[2]].img.contains("explosion") || Server.map[l[0]][c[0]].img.startsWith("powerup-")) && 
         (Server.map[l[3]][c[3]].img.equals("floor-1") || Server.map[l[3]][c[3]].img.contains("explosion") || Server.map[l[0]][c[0]].img.startsWith("powerup-"))
      ) 
         return true; //will be in a valid coordinate

      if (
         (Server.map[l[0]][c[0]].img.contains("block") || Server.map[l[0]][c[0]].img.contains("wall")) || 
         (Server.map[l[1]][c[1]].img.contains("block") || Server.map[l[1]][c[1]].img.contains("wall")) ||
         (Server.map[l[2]][c[2]].img.contains("block") || Server.map[l[2]][c[2]].img.contains("wall")) || 
         (Server.map[l[3]][c[3]].img.contains("block") || Server.map[l[3]][c[3]].img.contains("wall"))
      ) 
         return false; //will be on a wall



   // RELATIVE TO THE PREVIOUS COORDINATE

   // 0: top left corner point
      x[0] = Const.VAR_X_SPRITES + Server.player[id].x + Const.RESIZE;
      y[0] = Const.VAR_Y_SPRITES + Server.player[id].y + Const.RESIZE;
   // 1: top right corner point
      x[1] = Const.VAR_X_SPRITES + Server.player[id].x + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[1] = Const.VAR_Y_SPRITES + Server.player[id].y + Const.RESIZE;
   // 2: bottom left corner point
      x[2] = Const.VAR_X_SPRITES + Server.player[id].x + Const.RESIZE;
      y[2] = Const.VAR_Y_SPRITES + Server.player[id].y + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
   // 3: bottom right corner point
      x[3] = Const.VAR_X_SPRITES + Server.player[id].x + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[3] = Const.VAR_Y_SPRITES + Server.player[id].y + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      
      for (int i = 0; i < 4; i++) { 
         c[i] = getColumnOfMap(x[i]);
         l[i] = getLineOfMap(y[i]);
      }

      if (
         Server.map[l[0]][c[0]].img.contains("bomb-planted") ||
         Server.map[l[1]][c[1]].img.contains("bomb-planted") ||
         Server.map[l[2]][c[2]].img.contains("bomb-planted") ||
         Server.map[l[3]][c[3]].img.contains("bomb-planted")
      ) 
         return true; //was on a bomb just planted, needs to move away
      
      return false;
   }

   void keyCodePressed(int keyCode) {
      switch (keyCode) {
         case KeyEvent.VK_W: 
            up = true; down = right = left = false;
            ClientManager.sendToAllClients(this.id + " newStatus up");
            break;
         case KeyEvent.VK_S: 
            down = true; up = right = left = false;
            ClientManager.sendToAllClients(this.id + " newStatus down");
            break;
         case KeyEvent.VK_D: 
            right = true; up = down = left = false;
            ClientManager.sendToAllClients(this.id + " newStatus right");
            break;
         case KeyEvent.VK_A: 
            left = true; up = down = right = false;
            ClientManager.sendToAllClients(this.id + " newStatus left");
            break;
      }
   }

   void keyCodeReleased(int keyCode) {
      if (keyCode != KeyEvent.VK_W && keyCode != KeyEvent.VK_S && keyCode != KeyEvent.VK_D && keyCode != KeyEvent.VK_A)
         return;

      ClientManager.sendToAllClients(this.id + " stopStatusUpdate");
      switch (keyCode) {
         case KeyEvent.VK_W: up = false; break;
         case KeyEvent.VK_S: down = false; break;
         case KeyEvent.VK_D: right = false; break;
         case KeyEvent.VK_A: left = false; break;
      }
   }
}