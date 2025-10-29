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
         int rawId = Client.in.nextInt(); // client id (can be -1 for system)
         this.p = fromWhichPlayerIs(rawId);
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
         else if (str.equals("potionEffect")) {
            String type = Client.in.next();
            int x = Client.in.nextInt();
            int y = Client.in.nextInt();
            int radius = Client.in.nextInt();
            int duration = Client.in.nextInt();
            Game.handlePotionEffect(type, x, y, radius, duration);
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
      else if (str.equals("chat")) {
         String msg = Client.in.nextLine();
         if (msg == null) msg = "";
         msg = msg.trim();
         int senderId = Client.id;
         if (rawId < 0) senderId = -1;
         else if (p == Game.enemy1) senderId = (Client.id+1)%Const.QTY_PLAYERS;
         else if (p == Game.enemy2) senderId = (Client.id+2)%Const.QTY_PLAYERS;
         else if (p == Game.enemy3) senderId = (Client.id+3)%Const.QTY_PLAYERS;
         else if (p == Game.you) senderId = Client.id;
         Game.handleChat(senderId, msg);
         Game.you.panel.repaint();
      }
         else if (str.equals("disconnect")) {
            // Optional reason on the remainder of the line
            String reason = "";
            try { reason = Client.in.nextLine().trim(); } catch (Exception ex) {}
            // Show a brief dialog and exit
            try {
               javax.swing.JOptionPane.showMessageDialog(null,
                  (reason != null && !reason.isEmpty() ? reason : "Disconnected by server"),
                  "Disconnected", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {}
            System.exit(0);
         }
         else if (str.equals("potionPicked")) {
            // no local state tracked beyond visuals for now
            Client.in.next(); // type
         }
      }
         // Server connection closed. Exit the client app.
         try { Client.in.close(); } catch (Exception ex) {}
         System.exit(0);
   }
}