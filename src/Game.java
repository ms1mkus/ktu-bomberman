import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Game extends JPanel {

   private static final long serialVersionUID = 1L;
   static Player you, enemy1, enemy2, enemy3;
   private static java.util.HashMap<Long, BulletData> activeBullets = new java.util.HashMap<>();
   
   private boolean mousePressed = false;
   private int mouseX, mouseY;
   private Thread shootingThread = null;
   
   private static class BulletData {
      int x, y, direction;
      String spriteType;
      
      BulletData(int x, int y, int direction, String spriteType) {
         this.x = x; this.y = y; this.direction = direction; 
         this.spriteType = spriteType;
      }
   }

   Game(int width, int height) {
      setPreferredSize(new Dimension(width, height));
      
      addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (you.alive && Client.out != null) {
               mousePressed = true;
               mouseX = e.getX();
               mouseY = e.getY();
               
               Client.out.println("shoot " + mouseX + " " + mouseY);
               
               startContinuousShooting();
            }
         }
         
         @Override
         public void mouseReleased(MouseEvent e) {
            mousePressed = false;
            stopContinuousShooting();
         }
      });
      
      addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            if (mousePressed) {
               mouseX = e.getX();
               mouseY = e.getY();
            }
         }
      });
      
      try {
         System.out.print("Initializing players...");
         you = new Player(Client.id, this);
         enemy1 = new Player((Client.id+1)%Const.QTY_PLAYERS, this);
         enemy2 = new Player((Client.id+2)%Const.QTY_PLAYERS, this);
         enemy3 = new Player((Client.id+3)%Const.QTY_PLAYERS, this);
      } catch (InterruptedException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");

      System.out.println("My player: " + Sprite.personColors[Client.id]);
   }

   //draws components, called by paint() and repaint()
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawMap(g);
      drawBlockHealth(g);
      drawBullets(g);
      enemy1.draw(g);
      enemy2.draw(g);
      enemy3.draw(g);
      you.draw(g);
      
      // System.out.format("%s: %s [%04d, %04d]\n", Game.you.color, Game.you.status, Game.you.x, Game.you.y);;
      Toolkit.getDefaultToolkit().sync();
   }
   
   void drawMap(Graphics g) {
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            g.drawImage(
               Sprite.ht.get(Client.map[i][j].img), 
               Client.map[i][j].x, Client.map[i][j].y, 
               Const.SIZE_SPRITE_MAP, Const.SIZE_SPRITE_MAP, null
            );
   }
   
   void drawBullets(Graphics g) {
      for (BulletData bullet : activeBullets.values()) {
         if (bullet != null) {
            java.awt.Image bulletSprite = Sprite.ht.get("bullet/" + bullet.spriteType + "-1");
            
            if (bulletSprite != null) {
               int spriteSize = 32;
               int drawX = bullet.x - spriteSize / 2;
               int drawY = bullet.y - spriteSize / 2;
               
               g.drawImage(bulletSprite, drawX, drawY, spriteSize, spriteSize, null);
            } else {
               g.setColor(java.awt.Color.WHITE);
               g.fillOval(bullet.x - 5, bullet.y - 5, 10, 10);
            }
         }
      }
   }
   
   void drawBlockHealth(Graphics g) {
      for (int i = 0; i < Const.LIN; i++) {
         for (int j = 0; j < Const.COL; j++) {
            if (Client.map[i][j].img.equals("block")) {
               String key = j + "," + i;
               int health = BlockHealthManager.getHealth(key);
               
               if (health < 1000) {
                  double healthPercent = (double) health / 1000;
                  int x = j * Const.SIZE_SPRITE_MAP;
                  int y = i * Const.SIZE_SPRITE_MAP;
                  
                  int barWidth = (int)(Const.SIZE_SPRITE_MAP * healthPercent);
                  int barHeight = 6;
                  
                  g.setColor(java.awt.Color.RED);
                  g.fillRect(x, y - 10, Const.SIZE_SPRITE_MAP, barHeight);
                  
                  if (healthPercent > 0.6) {
                     g.setColor(java.awt.Color.GREEN);
                  } else if (healthPercent > 0.3) {
                     g.setColor(java.awt.Color.ORANGE);
                  } else {
                     g.setColor(java.awt.Color.RED);
                  }
                  g.fillRect(x, y - 10, barWidth, barHeight);
                  
                  g.setColor(java.awt.Color.BLACK);
                  g.drawRect(x, y - 10, Const.SIZE_SPRITE_MAP, barHeight);
               }
            }
         }
      }
   }
   
   static void handleBulletUpdate(String bulletData) {
      String[] parts = bulletData.split(" ");
      String action = parts[0];
      
      if (action.equals("create")) {
         long bulletId = Long.parseLong(parts[1]);
         int x = Integer.parseInt(parts[2]);
         int y = Integer.parseInt(parts[3]);
         int direction = Integer.parseInt(parts[4]);
         String spriteType = parts[5];
         
         activeBullets.put(bulletId, new BulletData(x, y, direction, spriteType));
      } else if (action.equals("move")) {
         long bulletId = Long.parseLong(parts[1]);
         int x = Integer.parseInt(parts[2]);
         int y = Integer.parseInt(parts[3]);
         
         BulletData bullet = activeBullets.get(bulletId);
         if (bullet != null) {
            bullet.x = x;
            bullet.y = y;
         }
      } else if (action.equals("destroy")) {
         long bulletId = Long.parseLong(parts[1]);
         activeBullets.remove(bulletId);
      }
   }
   
   static void handleBlockHealth(String blockKey, int health) {
      BlockHealthManager.setHealth(blockKey, health);
   }

   static void setSpriteMap(String keyWord, int l, int c) {
      Client.map[l][c].img = keyWord;
   }
   
   private void startContinuousShooting() {
      stopContinuousShooting();
      
      shootingThread = new Thread(() -> {
         try {
            Thread.sleep(200);
            
            while (mousePressed && you.alive && Client.out != null) {
               Client.out.println("shoot " + mouseX + " " + mouseY);
               
               Thread.sleep(200);
            }
         } catch (InterruptedException e) {
         }
      });
      
      shootingThread.setDaemon(true);
      shootingThread.start();
   }
   
   private void stopContinuousShooting() {
      if (shootingThread != null && shootingThread.isAlive()) {
         shootingThread.interrupt();
         shootingThread = null;
      }
   }
}