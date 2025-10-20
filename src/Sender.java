import java.awt.event.*;

//listens while the window (JFrame) is focused
public class Sender extends KeyAdapter {
   int lastKeyCodePressed;
   
   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE) // dropping a bomb
         Client.out.println("pressedSpace " + Game.you.x + " " + Game.you.y);
      else if (e.getKeyCode() == KeyEvent.VK_C)  // building a wall
      {
         String dir = Sprite.getPersonSpriteFaceDirection(Game.you.status);

         Client.out.println("build_wall " + dir);
      }
      else if (e.getKeyCode() == KeyEvent.VK_X) // removing last placed wall
      {
         Client.out.println("removing_wall");
      }
      else if (isNewKeyCode(e.getKeyCode())) // moving
         Client.out.println("keyCodePressed " + e.getKeyCode());
      }
      
   public void keyReleased(KeyEvent e) {
      Client.out.println("keyCodeReleased " + e.getKeyCode());
      lastKeyCodePressed = -1; //the next key will always be new
   }
   
   boolean isNewKeyCode(int keyCode) {
      boolean ok = (keyCode != lastKeyCodePressed) ? true : false;
      lastKeyCodePressed = keyCode;
      return ok;
   }
}