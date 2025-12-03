// Client-side facade to send chat messages in a uniform way
class ChatFacade {
    static void sendBroadcast(String text) {
        ChatClient.getInstance().sendBroadcast(text);
    }

    static void sendPrivate(int toId, String text) {
        ChatClient.getInstance().sendPrivate(toId, text);
    }

    static void sendMute(int targetId) {
        ChatClient.getInstance().sendMute(targetId);
    }

    static void sendUnmute(int targetId) {
        ChatClient.getInstance().sendUnmute(targetId);
    }

    static void sendVoteKick(int targetId) {
        VoteClient.getInstance().sendVoteKick(targetId);
    }

    static void sendVoteMute(int targetId) {
        VoteClient.getInstance().sendVoteMute(targetId);
    }

    static void sendVote(String type, int targetId, boolean yes) {
        VoteClient.getInstance().sendVote(type, targetId, yes);
    }

    static void sendHelp() {
        ChatClient.getInstance().sendHelp();
    }

    static void sendWeather(String query) {
        WeatherClient.getInstance().sendWeather(query);
    }
}
