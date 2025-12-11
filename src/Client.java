import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.*;

public class Client {

   private Socket socket = null;
   static PrintStream out = null;
   static Scanner in = null;
   static int id;

   final static int rateStatusUpdate = 115;
   static Coordinate map[][] = new Coordinate[Const.LIN][Const.COL];

   static Coordinate spawn[] = new Coordinate[Const.QTY_PLAYERS];
   static boolean alive[] = new boolean[Const.QTY_PLAYERS];
   static boolean singlePlayerMode = false;

   static void restoreState(GameStateMemento memento) {
      singlePlayerMode = true; // Stop receiving updates
      
      // Restore Map
      Coordinate[][] savedMap = memento.getMapState();
      for (int i = 0; i < Const.LIN; i++) {
          for (int j = 0; j < Const.COL; j++) {
              map[i][j].img = savedMap[i][j].img;
              map[i][j].x = savedMap[i][j].x;
              map[i][j].y = savedMap[i][j].y;
          }
      }
  
      // Restore Players
      GameStateMemento.PlayerState[] savedPlayers = memento.getPlayerStates();
      Player[] currentPlayers = new Player[] { Game.you, Game.enemy1, Game.enemy2, Game.enemy3 };
      
      for (int i = 0; i < currentPlayers.length; i++) {
          if (currentPlayers[i] != null && savedPlayers[i] != null) {
              currentPlayers[i].x = savedPlayers[i].x;
              currentPlayers[i].y = savedPlayers[i].y;
              currentPlayers[i].status = savedPlayers[i].status;
              currentPlayers[i].alive = savedPlayers[i].alive;
              if (currentPlayers[i].sc != null) {
                   String fullStatus = savedPlayers[i].status;
                   // Extract base status (e.g., "down" from "down-2")
                   String baseStatus = fullStatus.split("-")[0];
                   currentPlayers[i].sc.setLoopStatus(baseStatus);
              }
          }
      }
      
      // Repaint
      if (Game.you != null && Game.you.panel != null) {
          Game.you.panel.repaint();
      }
  }

   Client(String host, int porta) {
      try {
         System.out.print("Establishing connection with server...");
         this.socket = new Socket(host, porta);
         out = new PrintStream(socket.getOutputStream(), true);  //to send to server
         in = new Scanner(socket.getInputStream()); //to receive from server
      } 
      catch (UnknownHostException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      } 
      catch (IOException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");
      
      receiveInitialSettings();
      new Receiver().start();
   }

   void receiveInitialSettings() {
      id = in.nextInt();

      //map
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, in.next());
      
      //initial status (alive or dead) of all players
      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         Client.alive[i] = in.nextBoolean();

      //initial coordinates of all players
      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         Client.spawn[i] = new Coordinate(in.nextInt(), in.nextInt());
            }
   
   public static void main(String[] args) {
      new Client("127.0.0.1", 8383);

      Sender sender = new Sender();

      Window window = new Window(sender);
      Console console = new Console(window, sender);
   }
}

class Window extends JFrame {
   private static final long serialVersionUID = 1L;

   Window(Sender sender) {
      Sprite.loadImages();
      Sprite.setMaxLoopStatus();

      add(new Game(Const.COL*Const.SIZE_SPRITE_MAP, Const.LIN*Const.SIZE_SPRITE_MAP));
      setTitle("bomberman");
      pack();
      setVisible(true);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      addKeyListener(sender);
      addKeyListener(new java.awt.event.KeyAdapter() {
         @Override
         public void keyPressed(java.awt.event.KeyEvent e) {
            Game game = (Game) getContentPane().getComponent(0);

            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F1) {
               game.toggleMapProtanopia();
            }
            else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F2) {
               game.togglePlayerProtanopia();
            }
            else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F12) {
               new GameWorld().accept(new WinterVisitor());
               game.repaint();
            }
         }
      });
   }
}

class Console extends JFrame
{
    private static final long serialVersionUID = 1L;

    public static JTextArea out;
    private JTextField input;


    public Console(Window gameWindow, Sender sender) {
        setTitle("Console");

        out = new JTextArea(20, 50);
        out.setEditable(false);
        JScrollPane scroll = new JScrollPane(out);

        input = new JTextField();
        input.addActionListener(e -> {
            String cmd = input.getText();
            input.setText("");

            log("> " + cmd);

            sender.sendConsoleCommand(cmd);

        });

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

        pack();
        int x = gameWindow.getX() + gameWindow.getWidth();
        int y = gameWindow.getY();
        setLocation(x, y);
        setVisible(true);
    }

    public static void log(String msg) {
        out.append(msg + "\n");
        out.setCaretPosition(out.getDocument().getLength()); // auto scroll
    }
}