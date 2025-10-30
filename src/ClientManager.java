import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//for each client that connects to the server, a new thread is created to handle it
class ClientManager extends Thread {
   static List<PrintStream> listOutClients = new ArrayList<PrintStream>();
   static PrintStream[] outById = new PrintStream[Const.QTY_PLAYERS];
   static ClientManager[] byId = new ClientManager[Const.QTY_PLAYERS];

   static void sendToAllClients(String outputLine) {
      // Prefer indexed outputs to avoid any stale list issues
      for (int i = 0; i < Const.QTY_PLAYERS; i++) {
         PrintStream out = outById[i];
         if (out != null) out.println(outputLine);
      }
   }

   static void sendToClient(int id, String outputLine) {
      PrintStream out = outById[id];
      if (out != null) out.println(outputLine);
   }

   private Socket clientSocket = null;
   private Scanner in = null;
   private PrintStream out = null;
   private int id;

   CoordinatesThrowerHandler ct;
   MapUpdatesThrowerHandler mt;
   BulletThrowerHandler bt;

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
   outById[id] = out;
     byId[id] = this;
      Server.player[id].logged = true;
      Server.player[id].alive = true;
   sendInitialSettings(); // sends a single string

   // deliver recent chat backlog to the newly connected client
   MessageFacade.deliverBacklogTo(id);

   //notifies already connected clients
      for (PrintStream outClient: listOutClients)
         if (outClient != this.out)
            outClient.println(id + " playerJoined");
   }

   public void run() {
   while (in.hasNextLine()) { // connection established with client this.id
         String str[] = in.nextLine().split(" ");
         
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
         else if (str[0].equals("chat_all")) {
            String message = String.join(" ", java.util.Arrays.copyOfRange(str, 1, str.length));
            MessageFacade.broadcastChat(id, message);
         }
         else if (str[0].equals("chat_to") && str.length >= 3) {
            int toId = Integer.parseInt(str[1]);
            String message = String.join(" ", java.util.Arrays.copyOfRange(str, 2, str.length));
            MessageFacade.privateChat(id, toId, message);
         }
         else if (str[0].equals("chat_mute") && str.length >= 2) {
            try {
               int target = Integer.parseInt(str[1]);
               MessageFacade.mutePlayer(id, target);
               MessageFacade.sendSystemTo(id, "Muted player " + target + ".");
            } catch (NumberFormatException ex) {
               MessageFacade.sendError(id, "Usage: /mute <id>");
            }
         }
         else if (str[0].equals("chat_unmute") && str.length >= 2) {
            try {
               int target = Integer.parseInt(str[1]);
               MessageFacade.unmutePlayer(id, target);
               MessageFacade.sendSystemTo(id, "Unmuted player " + target + ".");
            } catch (NumberFormatException ex) {
               MessageFacade.sendError(id, "Usage: /unmute <id>");
            }
         }
         else if (str[0].equals("chat_votekick") && str.length >= 2) {
            try {
               int target = Integer.parseInt(str[1]);
               MessageFacade.voteKick(id, target);
            } catch (NumberFormatException ex) {
               MessageFacade.sendError(id, "Usage: /votekick <id>");
            }
         }
         else if (str[0].equals("chat_votemute") && str.length >= 2) {
            try {
               int target = Integer.parseInt(str[1]);
               MessageFacade.voteMute(id, target);
            } catch (NumberFormatException ex) {
               MessageFacade.sendError(id, "Usage: /votemute <id>");
            }
         }
         else if (str[0].equals("chat_weather") && str.length >= 2) {
            String query = String.join(" ", java.util.Arrays.copyOfRange(str, 1, str.length));
            MessageFacade.requestWeather(id, query);
         }
         else if (str[0].equals("chat_vote") && str.length >= 4) {
            // chat_vote <kick|mute> <id> <yes|no>
            String type = str[1];
            try {
               int target = Integer.parseInt(str[2]);
               String yn = str[3].toLowerCase();
               boolean yes = yn.equals("yes") || yn.equals("y");
               if (type.equalsIgnoreCase("kick")) {
                  MessageFacade.voteKickChoice(id, target, yes);
               } else if (type.equalsIgnoreCase("mute")) {
                  MessageFacade.voteMuteChoice(id, target, yes);
               } else {
                  MessageFacade.sendError(id, "Usage: /vote <kick|mute> <id> <yes|no>");
               }
            } catch (NumberFormatException ex) {
               MessageFacade.sendError(id, "Usage: /vote <kick|mute> <id> <yes|no>");
            }
         }
         else if (str[0].equals("chat_help")) {
            MessageFacade.sendSystemTo(id, "Commands:");
            MessageFacade.sendSystemTo(id, "/w <id> <msg> — private message");
            MessageFacade.sendSystemTo(id, "/mute <id> | /unmute <id>");
            MessageFacade.sendSystemTo(id, "/votekick <id> | /votemute <id>");
            MessageFacade.sendSystemTo(id, "/vote <kick|mute> <id> <yes|no>");
            MessageFacade.sendSystemTo(id, "/weather <city or country>");
         }
         else if (str[0].equals("throw_potion") && Server.player[id].alive) {
            if (PotionManager.playerHasPotion(id)) {
               int tx = Integer.parseInt(str[1]);
               int ty = Integer.parseInt(str[2]);
               Potion p = PotionManager.usePlayerPotion(id);
               PotionManager.applyPotionEffect(id, p, tx, ty);
            }
         }
      }
      clientDesconnected();
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

   void clientDesconnected() {
      listOutClients.remove(out);
      outById[id] = null;
      byId[id] = null;
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

   static void disconnectPlayer(int targetId) {
      if (targetId < 0 || targetId >= Const.QTY_PLAYERS) return;
      ClientManager cm = byId[targetId];
      if (cm == null) return;
      try {
         // Politely instruct client to close before cutting the socket
         if (cm.out != null) {
            cm.out.println(-1 + " disconnect KICKED");
            cm.out.flush();
         }
         // Give a tiny moment to flush the line out
         try { Thread.sleep(50); } catch (InterruptedException e) {}
         cm.clientSocket.close();
      } catch (IOException e) {
      }
   }
}