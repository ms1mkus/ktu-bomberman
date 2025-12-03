class VoteClient {
    private static VoteClient instance = new VoteClient();
    public static VoteClient getInstance() { return instance; }
    private VoteClient() {}

    public void sendVoteKick(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_votekick " + targetId);
        }
    }

    public void sendVoteMute(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_votemute " + targetId);
        }
    }

    public void sendVote(String type, int targetId, boolean yes) {
        if (Client.out != null) {
            Client.out.println("chat_vote " + type + " " + targetId + " " + (yes ? "yes" : "no"));
        }
    }
}
