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
         if (Client.singlePlayerMode) break; // Stop processing updates in single player mode

         this.p = fromWhichPlayerIs(Client.in.nextInt()); //client id
         str = Client.in.next();

         if (str.equals("stateChange")) {
            String animationType = Client.in.next();
            if (p != null && p.sc != null) {
               p.sc.setLoopStatus(animationType);
            }
         }
         else if (str.equals("mapUpdate")) {
            String img = Client.in.next();
            int l = Client.in.nextInt();
            int c = Client.in.nextInt();
            
            if (img.contains("bomb-planted")) {
               GameStateCaretaker.saveState();
            }

            Game.setSpriteMap(img, l, c);
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
            String status = Client.in.next();
            p.sc.setLoopStatus(status);

            if (p == Game.you && status.startsWith("dead") && !Client.singlePlayerMode) {
               javax.swing.SwingUtilities.invokeLater(() -> {
                   java.util.List<GameStateMemento> mementos = GameStateCaretaker.getMementos();
                   if (mementos.isEmpty()) return;

                   Object[] options = new Object[mementos.size()];
                   for (int i = 0; i < mementos.size(); i++) {
                       options[i] = "State " + (i + 1) + " (" + mementos.get(i).getTimestamp() + ")";
                   }

                   String s = (String) javax.swing.JOptionPane.showInputDialog(
                           null,
                           "You died! Select a state to replay in Single Player Mode:",
                           "Game Over - Memento",
                           javax.swing.JOptionPane.PLAIN_MESSAGE,
                           null,
                           options,
                           options[options.length - 1]);

                   if ((s != null) && (s.length() > 0)) {
                       for (int i = 0; i < options.length; i++) {
                           if (options[i].equals(s)) {
                               Client.restoreState(mementos.get(i));
                               break;
                           }
                       }
                   }
               });
            }
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
         else if (str.equals("potionPicked")) {
            // no local state tracked beyond visuals for now
            Client.in.next(); // type
         }
         else if (str.equals("console_res"))
         {
             String consoleResponseMessage = Client.in.nextLine().trim();
             Console.log(consoleResponseMessage);
         }

      }
      Client.in.close();
   }
}