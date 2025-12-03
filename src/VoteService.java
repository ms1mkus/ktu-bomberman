import java.util.HashMap;
import java.util.Map;

class VoteService {
    private static VoteService instance = new VoteService();
    public static VoteService getInstance() { return instance; }
    private VoteService() {}

    private static final long VOTE_DURATION_MS = 30_000L;

    private enum VoteType { KICK, MUTE }

    private static class Vote {
        final VoteType type;
        final int targetId;
        final long startMs;
        final long endMs;
        // vote per player: 1 = yes, -1 = no, 0 = not voted
        final byte[] votes = new byte[Const.QTY_PLAYERS];
        Vote(VoteType type, int targetId) {
            this.type = type;
            this.targetId = targetId;
            this.startMs = System.currentTimeMillis();
            this.endMs = this.startMs + VOTE_DURATION_MS;
        }

        boolean setVote(int fromId, boolean yes) {
            if (fromId < 0 || fromId >= votes.length) return false;
            if (votes[fromId] != 0) return false; // already voted
            votes[fromId] = (byte)(yes ? 1 : -1);
            return true;
        }

        int voteOf(int playerId) {
            if (playerId < 0 || playerId >= votes.length) return 0;
            return votes[playerId];
        }
    }

    // key: type + ":" + targetId
    private final Map<String, Vote> activeVotes = new HashMap<>();

    public void voteKick(int fromId, int targetId) {
        startOrVote(fromId, targetId, VoteType.KICK, true);
    }

    public void voteMute(int fromId, int targetId) {
        startOrVote(fromId, targetId, VoteType.MUTE, true);
    }

    public void voteKickChoice(int fromId, int targetId, boolean yes) {
        startOrVote(fromId, targetId, VoteType.KICK, yes);
    }

    public void voteMuteChoice(int fromId, int targetId, boolean yes) {
        startOrVote(fromId, targetId, VoteType.MUTE, yes);
    }

    private void startOrVote(int fromId, int targetId, VoteType type, boolean yes) {
        if (fromId < 0 || fromId >= Const.QTY_PLAYERS || ClientManager.outById[fromId] == null) return;
        if (targetId < 0 || targetId >= Const.QTY_PLAYERS || !ClientManagerIsOnline(targetId)) {
            ChatService.getInstance().sendError(fromId, "Target " + targetId + " is not online.");
            return;
        }
        if (fromId == targetId && type == VoteType.KICK) {
            ChatService.getInstance().sendError(fromId, "You cannot kick yourself.");
            return;
        }
        // expire old vote if needed
        String key = type.name() + ":" + targetId;
        Vote v = activeVotes.get(key);
        long now = System.currentTimeMillis();
        if (v != null && now > v.endMs) {
            activeVotes.remove(key);
            ChatService.getInstance().sendSystemAll("Vote to " + type.name().toLowerCase() + " player " + targetId + " expired.");
            v = null;
        }
        // create if absent
        if (v == null) {
            v = new Vote(type, targetId);
            activeVotes.put(key, v);
            ChatService.getInstance().sendSystemAll("Vote started to " + type.name().toLowerCase() + " player " + targetId + ". Type /" +
                (type == VoteType.KICK ? "votekick " : "votemute ") + targetId + " to vote yes, or /vote " +
                (type == VoteType.KICK ? "kick " : "mute ") + targetId + " no to vote no. (30s)");
        }
        // record vote
        if (v.setVote(fromId, yes)) {
            announceTally(v);
        } else {
            ChatService.getInstance().sendError(fromId, "You've already voted.");
        }
        // check success
        checkAndFinalize(v);
    }

    private void announceTally(Vote v) {
        int yes = countYes(v);
        int no = countNo(v);
        int needed = requiredYes(v.targetId);
        ChatService.getInstance().sendSystemAll("Vote to " + v.type.name().toLowerCase() + " player " + v.targetId + ": " + yes + "/" + needed + " yes (" + no + " no)");
    }

    private void checkAndFinalize(Vote v) {
        int yes = countYes(v);
        int needed = requiredYes(v.targetId);
        if (yes >= needed) {
            // Passed
            activeVotes.remove(v.type.name() + ":" + v.targetId);
            if (v.type == VoteType.KICK) {
                ChatService.getInstance().sendSystemAll("Vote passed. Kicking player " + v.targetId + ".");
                ClientManager.disconnectPlayer(v.targetId);
            } else {
                ChatService.getInstance().setServerMuted(v.targetId, true);
                ChatService.getInstance().sendSystemAll("Vote passed. Muted player " + v.targetId + ".");
            }
        }
    }

    private int countYes(Vote v) {
        int c = 0;
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            if (v.voteOf(i) == 1 && ClientManagerIsOnline(i)) c++;
        }
        return c;
    }

    private int countNo(Vote v) {
        int c = 0;
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            if (v.voteOf(i) == -1 && ClientManagerIsOnline(i)) c++;
        }
        return c;
    }

    private int requiredYes(int targetId) {
        int online = 0;
        for (int i = 0; i < Const.QTY_PLAYERS; i++) if (ClientManagerIsOnline(i)) online++;
        if (ClientManagerIsOnline(targetId)) online--; // exclude target from quorum
        if (online < 1) online = 1;
        return online / 2 + 1; // strict majority of voters
    }

    private boolean ClientManagerIsOnline(int id) {
        return id >= 0 && id < Const.QTY_PLAYERS && ClientManager.outById[id] != null;
    }
}
