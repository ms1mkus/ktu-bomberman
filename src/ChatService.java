import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class ChatService {
    private static ChatService instance = new ChatService();
    public static ChatService getInstance() { return instance; }
    private ChatService() {}

    // Basic formatting and limits
    private static final int MAX_LEN = 200;
    private static final int BACKLOG_LIMIT = 30;
    private static final long WINDOW_MS = 5000L; // rate limit window
    private static final int MAX_PER_WINDOW = 5; // max messages per window per user

    // Simple backlog (broadcast and system only)
    private final LinkedList<MessageRecord> backlog = new LinkedList<>();

    // Rate limiting state
    private final long[] windowStartMs = new long[Const.QTY_PLAYERS];
    private final int[] countInWindow = new int[Const.QTY_PLAYERS];
    private final boolean[] serverMuted = new boolean[Const.QTY_PLAYERS];

    // Mute matrix: muted[receiver][sender] == true means receiver will not see sender's messages
    private final boolean[][] muted = new boolean[Const.QTY_PLAYERS][Const.QTY_PLAYERS];

    private static class MessageRecord {
        final int fromId; // use -1 for system
        final String text; // already formatted with timestamp and tags
        MessageRecord(int fromId, String text) {
            this.fromId = fromId;
            this.text = text;
        }
    }

    public void broadcastChat(int fromId, String message) {
        if (!allowAndNormalizeMessage(fromId, message)) return;
        String formatted = formatWithTimestamp(message);
        // Add to backlog
        synchronized (backlog) {
            backlog.add(new MessageRecord(fromId, formatted));
            while (backlog.size() > BACKLOG_LIMIT) backlog.removeFirst();
        }
        // Deliver to each client honoring mute lists
        for (int toId = 0; toId < Const.QTY_PLAYERS; toId++) {
            if (ClientManagerIsOnline(toId) && !muted[toId][fromId]) {
                ClientManager.sendToClient(toId, fromId + " chat " + formatted);
            }
        }
    }

    public void privateChat(int fromId, int toId, String message) {
        if (!allowAndNormalizeMessage(fromId, message)) return;
        if (toId < 0 || toId >= Const.QTY_PLAYERS || !ClientManagerIsOnline(toId)) {
            sendError(fromId, "User " + toId + " is not online.");
            return;
        }
        String formattedTo = formatWithTimestamp("[whisper] " + message);
        String formattedEcho = formatWithTimestamp("[to " + toId + "] " + message);
        // Respect recipient mute preference
        if (!muted[toId][fromId]) {
            ClientManager.sendToClient(toId, fromId + " chat " + formattedTo);
        }
        ClientManager.sendToClient(fromId, fromId + " chat " + formattedEcho);
    }

    public void sendSystemAll(String text) {
        String formatted = formatWithTimestamp("[sys] " + normalize(text));
        synchronized (backlog) {
            backlog.add(new MessageRecord(-1, formatted));
            while (backlog.size() > BACKLOG_LIMIT) backlog.removeFirst();
        }
        ClientManager.sendToAllClients(-1 + " chat " + formatted);
    }

    public void sendSystemTo(int toId, String text) {
        if (!ClientManagerIsOnline(toId)) return;
        String formatted = formatWithTimestamp("[sys] " + normalize(text));
        ClientManager.sendToClient(toId, -1 + " chat " + formatted);
    }

    public void sendError(int toId, String text) {
        if (!ClientManagerIsOnline(toId)) return;
        String formatted = formatWithTimestamp("[error] " + normalize(text));
        ClientManager.sendToClient(toId, -1 + " chat " + formatted);
    }

    public void deliverBacklogTo(int toId) {
        if (!ClientManagerIsOnline(toId)) return;
        List<MessageRecord> snapshot;
        synchronized (backlog) { snapshot = new ArrayList<>(backlog); }
        for (MessageRecord rec : snapshot) {
            // Deliver each line as-is
            ClientManager.sendToClient(toId, rec.fromId + " chat " + rec.text);
        }
    }

    public void mutePlayer(int receiverId, int senderId) {
        if (receiverId >= 0 && receiverId < Const.QTY_PLAYERS && senderId >= 0 && senderId < Const.QTY_PLAYERS) {
            muted[receiverId][senderId] = true;
        }
    }

    public void unmutePlayer(int receiverId, int senderId) {
        if (receiverId >= 0 && receiverId < Const.QTY_PLAYERS && senderId >= 0 && senderId < Const.QTY_PLAYERS) {
            muted[receiverId][senderId] = false;
        }
    }

    public void setServerMuted(int playerId, boolean isMuted) {
        if (playerId >= 0 && playerId < Const.QTY_PLAYERS) {
            serverMuted[playerId] = isMuted;
        }
    }

    private boolean ClientManagerIsOnline(int id) {
        return id >= 0 && id < Const.QTY_PLAYERS && ClientManager.outById[id] != null;
    }

    private boolean allowAndNormalizeMessage(int fromId, String message) {
        // Basic alive/logged check
        if (fromId < 0 || fromId >= Const.QTY_PLAYERS || ClientManager.outById[fromId] == null) return false;
        if (serverMuted[fromId]) {
            sendError(fromId, "You are muted by vote.");
            return false;
        }
        // Rate limiting token bucket per window
        long now = System.currentTimeMillis();
        if (now - windowStartMs[fromId] > WINDOW_MS) {
            windowStartMs[fromId] = now;
            countInWindow[fromId] = 0;
        }
        if (countInWindow[fromId] >= MAX_PER_WINDOW) {
            sendError(fromId, "You're sending messages too fast. Please slow down.");
            return false;
        }
        countInWindow[fromId]++;
        return true;
    }

    private String normalize(String message) {
        if (message == null) return "";
        String m = message.trim();
        if (m.length() > MAX_LEN) m = m.substring(0, MAX_LEN);
        return m;
    }

    private String formatWithTimestamp(String message) {
        String m = normalize(message);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String ts = sdf.format(new Date());
        return "[" + ts + "] " + m;
    }
}
