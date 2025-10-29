// Client-side facade to send chat messages in a uniform way
class ChatFacade {
    static void sendBroadcast(String text) {
        if (Client.out != null && text != null && !text.isBlank()) {
            Client.out.println("chat_all " + text);
        }
    }

    static void sendPrivate(int toId, String text) {
        if (Client.out != null && text != null && !text.isBlank()) {
            Client.out.println("chat_to " + toId + " " + text);
        }
    }

    static void sendMute(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_mute " + targetId);
        }
    }

    static void sendUnmute(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_unmute " + targetId);
        }
    }

    static void sendVoteKick(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_votekick " + targetId);
        }
    }

    static void sendVoteMute(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_votemute " + targetId);
        }
    }

    static void sendVote(String type, int targetId, boolean yes) {
        if (Client.out != null) {
            Client.out.println("chat_vote " + type + " " + targetId + " " + (yes ? "yes" : "no"));
        }
    }

    static void sendHelp() {
        if (Client.out != null) {
            Client.out.println("chat_help");
        }
    }

    static void sendWeather(String query) {
        if (Client.out != null && query != null && !query.isBlank()) {
            Client.out.println("chat_weather " + query);
        }
    }
}
