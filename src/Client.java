import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Client {

   private Socket socket = null;
   static PrintStream out = null;
   static Scanner in = null;
   static int id;

   final static int rateStatusUpdate = 115;
   static Coordinate map[][] = new Coordinate[Const.LIN][Const.COL];

   static Coordinate spawn[] = new Coordinate[Const.QTY_PLAYERS];
   static boolean alive[] = new boolean[Const.QTY_PLAYERS];

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
      new Window();
   }
}

class Window extends JFrame {
   private static final long serialVersionUID = 1L;

   Window() {
      Sprite.loadImages();
      Sprite.setMaxLoopStatus();
      
      setLayout(new java.awt.BorderLayout());
      Game gamePanel = new Game(Const.COL*Const.SIZE_SPRITE_MAP, Const.LIN*Const.SIZE_SPRITE_MAP);
      gamePanel.setFocusable(true);
      add(gamePanel, java.awt.BorderLayout.CENTER);

      javax.swing.JTextField chatField = new javax.swing.JTextField();
      chatField.setToolTipText("Type message. Use /w <id> <msg> for private");
      chatField.setFocusable(false); // enable focus only when user starts chat
      chatField.addActionListener(new java.awt.event.ActionListener() {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent e) {
            String text = chatField.getText().trim();
            if (!text.isEmpty()) {
               if (text.startsWith("/w ") || text.startsWith("/whisper ") || text.startsWith("/pm ")) {
                  String[] parts = text.split(" ", 3);
                  if (parts.length >= 3) {
                     try {
                        int toId = Integer.parseInt(parts[1]);
                        ChatFacade.sendPrivate(toId, parts[2]);
                     } catch (NumberFormatException ex) {
                        ChatFacade.sendBroadcast(text);
                     }
                  } else {
                     ChatFacade.sendBroadcast(text);
                  }
               } else if (text.startsWith("/mute ")) {
                  String[] parts = text.split(" ", 2);
                  if (parts.length == 2) {
                     try {
                        int target = Integer.parseInt(parts[1]);
                        ChatFacade.sendMute(target);
                     } catch (NumberFormatException ex) {
                        ChatFacade.sendBroadcast(text);
                     }
                  }
               } else if (text.startsWith("/unmute ")) {
                  String[] parts = text.split(" ", 2);
                  if (parts.length == 2) {
                     try {
                        int target = Integer.parseInt(parts[1]);
                        ChatFacade.sendUnmute(target);
                     } catch (NumberFormatException ex) {
                        ChatFacade.sendBroadcast(text);
                     }
                  }
               } else if (text.startsWith("/votekick ")) {
                  String[] parts = text.split(" ", 2);
                  if (parts.length == 2) {
                     try {
                        int target = Integer.parseInt(parts[1]);
                        ChatFacade.sendVoteKick(target);
                     } catch (NumberFormatException ex) {
                        ChatFacade.sendBroadcast(text);
                     }
                  }
               } else if (text.startsWith("/votemute ")) {
                  String[] parts = text.split(" ", 2);
                  if (parts.length == 2) {
                     try {
                        int target = Integer.parseInt(parts[1]);
                        ChatFacade.sendVoteMute(target);
                     } catch (NumberFormatException ex) {
                        ChatFacade.sendBroadcast(text);
                     }
                  }
               } else if (text.startsWith("/vote ")) {
                  // /vote <kick|mute> <id> <yes|no>
                  String[] parts = text.split(" ", 4);
                  if (parts.length >= 4) {
                     String type = parts[1];
                     try {
                        int target = Integer.parseInt(parts[2]);
                        String yn = parts[3].toLowerCase();
                        boolean yes = yn.equals("yes") || yn.equals("y");
                        ChatFacade.sendVote(type, target, yes);
                     } catch (NumberFormatException ex) {
                        ChatFacade.sendBroadcast(text);
                     }
                  }
               } else if (text.startsWith("/weather ")) {
                  String query = text.substring("/weather ".length()).trim();
                  if (!query.isEmpty()) {
                     ChatFacade.sendWeather(query);
                  }
               } else if (text.equals("/help")) {
                  ChatFacade.sendHelp();
               } else {
                  ChatFacade.sendBroadcast(text);
               }
               chatField.setText("");
               chatField.setFocusable(false);
               gamePanel.requestFocusInWindow();
            }
         }
      });
      add(chatField, java.awt.BorderLayout.SOUTH);

      setTitle("bomberman");
      pack();
      setVisible(true);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      // Prefer key events on the game panel to avoid focus issues
      gamePanel.addKeyListener(new Sender());

      // Global key binding: Enter starts chat (focuses chat field) when not already typing
      javax.swing.JComponent root = getRootPane();
      root.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
          .put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "START_CHAT");
      root.getActionMap().put("START_CHAT", new javax.swing.AbstractAction() {
         @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            if (!chatField.isFocusOwner()) {
               chatField.setFocusable(true);
               chatField.setText("");
               chatField.requestFocusInWindow();
            }
         }
      });

      // ESC cancels chat input and returns focus to the game
      chatField.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
               .put(javax.swing.KeyStroke.getKeyStroke("ESCAPE"), "CANCEL_CHAT");
      chatField.getActionMap().put("CANCEL_CHAT", new javax.swing.AbstractAction() {
         @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            chatField.setText("");
            chatField.setFocusable(false);
            gamePanel.requestFocusInWindow();
         }
      });

      // Ensure initial focus goes to the game panel so WASD works immediately
      javax.swing.SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
   }
}