//receives information from all clients
public class Receiver extends Thread {
   Player p;
   
   Player fromWhichPlayerIs(int id) {

      if (id == Client.id)
         return Game.you;
      else if (id == (Client.id+1)%Const.QTY_PLAYERS)
         return Game.enemy1;
      else if (id == (Client.id+2)%Const.QTY_PLAYERS)
         return Game.enemy2;
      else if (id == (Client.id+3)%Const.QTY_PLAYERS)
         return Game.enemy3;
      return null;
   }

   public void run() {
      String str;
      while (Client.in.hasNextLine()) {
         this.p = fromWhichPlayerIs(Client.in.nextInt()); //client id
         str = Client.in.next();

         if (str.equals("mapUpdate")) {
            Game.setSpriteMap(Client.in.next(), Client.in.nextInt(), Client.in.nextInt());
            Game.you.panel.repaint();
         }
         else if (str.equals("bulletUpdate")) {
            Game.handleBulletUpdate(Client.in.nextLine().trim());
            Game.you.panel.repaint();
         }
         else if (str.equals("blockHealth")) {
            String blockKey = Client.in.next();
            int health = Client.in.nextInt();
            Game.handleBlockHealth(blockKey, health);
            Game.you.panel.repaint();
         }
         else if (str.equals("newCoordinate")) {
            p.x = Client.in.nextInt();
            p.y = Client.in.nextInt();
            Game.you.panel.repaint();
         }
         else if (str.equals("newStatus")) {
            p.sc.setLoopStatus(Client.in.next());
         }
         else if (str.equals("stopStatusUpdate")) {
            p.sc.stopLoopStatus();
         }
         else if (str.equals("playerJoined")) {
            p.alive = true;
         }
         else if (str.equals("powerUp")) {
            String powerUpType = Client.in.next();
            if (powerUpType.equals("bigbomb")) {
               p.addBigBomb();
            }
            else if (powerUpType.equals("speedboost")) {
               p.addSpeedBoost();
            }
            else if (powerUpType.equals("ghost")) {
               p.addGhost();
            }
         }
         else if (str.equals("powerUpUsed")) {
            String powerUpType = Client.in.next();
            if (powerUpType.equals("bigbomb")) {
               p.abilities = new BasicPlayer();
            }
         }
      }
      Client.in.close();
   }
}