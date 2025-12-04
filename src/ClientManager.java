import jdk.jfr.Experimental;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

//for each client that connects to the server, a new thread is created to handle it
class ClientManager extends Thread {
   static List<PrintStream> listOutClients = new ArrayList<PrintStream>();

   static void sendToAllClients(String outputLine) {
      for (PrintStream outClient : listOutClients)
         outClient.println(outputLine);
   }

   private Socket clientSocket = null;
   private Scanner in = null;
   private PrintStream out = null;
   private int id;

   CoordinatesThrowerHandler ct;
   MapUpdatesThrowerHandler mt;
   BulletThrowerHandler bt;

   private int playerBombLine = -1;
   private int playerBombCol = -1;

   ClientManager(Socket clientSocket, int id) {
      this.id = id;
      this.clientSocket = clientSocket;
      this.ct = (CoordinatesThrowerHandler)ThrowerHandlerFactory.makeHandler(ThrowerHandlerType.COORDINATES, id);
      this.mt = (MapUpdatesThrowerHandler)ThrowerHandlerFactory.makeHandler(ThrowerHandlerType.MAP_UPDATES, id);
      this.bt = (BulletThrowerHandler)ThrowerHandlerFactory.makeHandler(ThrowerHandlerType.BULLETS, id);


      try {
         System.out.print("Starting connection with player " + this.id + "...");
         this.in = new Scanner(clientSocket.getInputStream()); // to receive from client
         this.out = new PrintStream(clientSocket.getOutputStream(), true); // to send to client
      } catch (IOException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");

      listOutClients.add(out);
      Server.player[id].logged = true;
      Server.player[id].alive = true;
   sendInitialSettings(); // sends a single string

   //notifies already connected clients
      for (PrintStream outClient: listOutClients)
         if (outClient != this.out)
            outClient.println(id + " playerJoined");
   }

   public void run() {
   while (in.hasNextLine()) { // connection established with client this.id


        String receivedString = in.nextLine();

         String str[] = receivedString.split(" ");

         if (str[0].equals("keyCodePressed") && Server.player[id].alive)
         {
            ct.keyCodePressed(Integer.parseInt(str[1]));
         } 
         else if (str[0].equals("keyCodeReleased") && Server.player[id].alive) {
            ct.keyCodeReleased(Integer.parseInt(str[1]));
         } 
         else if (str[0].equals("pressedSpace") && Server.player[id].numberOfBombs >= 1) {
            Server.player[id].numberOfBombs--;
            mt.setBombPlanted(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
            playerBombLine = mt.l;
            playerBombCol = mt.c;
            System.out.println("DEBUG: Player " + id + " planted bomb at (" + playerBombLine + "," + playerBombCol + ")");
      }
      else if (str[0].equals("toggleBombCover") && Server.player[id].alive) {
         System.out.println("DEBUG: Player " + id + " pressed Y");
         handleBombCoverToggle();
      }
         else if (str[0].equals("build_wall") && Server.player[id].alive)
         {
            mt.setBuildableWall(str[1]);
         }
         else if (str[0].equals("removing_wall") && Server.player[id].alive)
         {
            mt.undoBuildableWall();
         }
         else if (str[0].equals("shoot") && Server.player[id].alive) {
            int startX = Server.player[id].x + Const.WIDTH_SPRITE_PLAYER / 2;
            int startY = Server.player[id].y + Const.HEIGHT_SPRITE_PLAYER / 2;
            bt.setBulletFired(startX, startY, Integer.parseInt(str[1]), Integer.parseInt(str[2]));
         }
         else if (str[0].equals("throw_potion") && Server.player[id].alive) {
            if (PotionManager.playerHasPotion(id)) {
               int tx = Integer.parseInt(str[1]);
               int ty = Integer.parseInt(str[2]);
               Potion p = PotionManager.usePlayerPotion(id);
               PotionManager.applyPotionEffect(id, p, tx, ty);
            }
         }
         else if (str.length >= 2 && str[0].equals("console"))
         {
             String consoleCommand = receivedString.substring(receivedString.indexOf(' ') + 1);

             Lexer lexer = new Lexer(consoleCommand);
             List<Token> tokens = lexer.tokenize();

             Parser parser = new Parser(tokens);
             Expression e = parser.parse();

             String messageBack = e.interpret(id);

             SendConsoleResponse(messageBack);


         }
      }
      clientDesconnected();
   }
   private void handleBombCoverToggle() {
   PlayerData player = Server.player[id];
   
   System.out.println("DEBUG: handleBombCoverToggle called. Bomb pos: (" + playerBombLine + "," + playerBombCol + ")");
   
   if (playerBombLine == -1 || playerBombCol == -1) {
      System.out.println("DEBUG: No bomb tracked for player " + id);
      return;
   }
   
   int line = playerBombLine;
   int col = playerBombCol;
   
   // Check what's actually on the map
   String tile = Server.map[line][col].img;
   System.out.println("DEBUG: Tile at (" + line + "," + col + ") is: " + tile);
   
   if (!tile.contains("bomb")) {
      System.out.println("DEBUG: No bomb at that position");
      playerBombLine = -1;
      playerBombCol = -1;
      return;
   }
   
   // Check current cover state
   boolean currentlyCovered = player.isBombCovered(line, col);
   System.out.println("DEBUG: Currently covered? " + currentlyCovered);
   
   if (currentlyCovered) {
      // Remove cover
      player.removeBombCover();
      MapUpdatesThrowerHandler.changeMap("bomb-planted-0", line, col);
      System.out.println("DEBUG: Removed cover from (" + line + "," + col + ")");
   } else {
      // Add cover
      player.setBombCover(line, col);
      MapUpdatesThrowerHandler.changeMap("bomb-covered", line, col);
      System.out.println("DEBUG: Added cover to (" + line + "," + col + ")");
   }
   
   // Verify
   System.out.println("DEBUG: Now covered? " + player.isBombCovered(line, col));
}

   void sendInitialSettings() {
      out.print(id);
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            out.print(" " + Server.map[i][j].img);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         out.print(" " + Server.player[i].alive);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         out.print(" " + Server.player[i].x + " " + Server.player[i].y);
      out.print("\n");
   }

   void SendConsoleResponse(String message)
   {
       out.print(id + " console_res " + message + "\n");
   }

   void clientDesconnected() {
      listOutClients.remove(out);
      Server.player[id].logged = false;
      try {
         System.out.print("Closing connection with player " + this.id + "...");
         in.close();
         out.close();
         clientSocket.close();
      } catch (IOException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");
   }
}