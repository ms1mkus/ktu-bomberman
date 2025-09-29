import java.awt.event.*;

//listens while the window (JFrame) is focused
public class Sender extends KeyAdapter {
   int lastKeyCodePressed;
   
   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE)
         Client.out.println("pressedSpace " + Game.getInstance().getYou().x + " " + Game.getInstance().getYou().y);
      else if (isNewKeyCode(e.getKeyCode()))
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