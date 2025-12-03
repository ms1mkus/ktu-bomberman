// Facade over server messaging specifics for chat
class MessageFacade {
    
    // Chat delegates
    static void broadcastChat(int fromId, String message) {
        ChatService.getInstance().broadcastChat(fromId, message);
    }

    static void privateChat(int fromId, int toId, String message) {
        ChatService.getInstance().privateChat(fromId, toId, message);
    }

    static void sendSystemAll(String text) {
        ChatService.getInstance().sendSystemAll(text);
    }

    static void sendSystemTo(int toId, String text) {
        ChatService.getInstance().sendSystemTo(toId, text);
    }

    static void sendError(int toId, String text) {
        ChatService.getInstance().sendError(toId, text);
    }

    static void deliverBacklogTo(int toId) {
        ChatService.getInstance().deliverBacklogTo(toId);
    }

    static void mutePlayer(int receiverId, int senderId) {
        ChatService.getInstance().mutePlayer(receiverId, senderId);
    }

    static void unmutePlayer(int receiverId, int senderId) {
        ChatService.getInstance().unmutePlayer(receiverId, senderId);
    }

    // Weather delegates
    static void requestWeather(int fromId, String query) {
        WeatherService.getInstance().requestWeather(fromId, query);
    }

    // Vote delegates
    static void voteKick(int fromId, int targetId) {
        VoteService.getInstance().voteKick(fromId, targetId);
    }

    static void voteMute(int fromId, int targetId) {
        VoteService.getInstance().voteMute(fromId, targetId);
    }

    static void voteKickChoice(int fromId, int targetId, boolean yes) {
        VoteService.getInstance().voteKickChoice(fromId, targetId, yes);
    }

    static void voteMuteChoice(int fromId, int targetId, boolean yes) {
        VoteService.getInstance().voteMuteChoice(fromId, targetId, yes);
    }
}
