class ChatClient {
    private static ChatClient instance = new ChatClient();
    public static ChatClient getInstance() { return instance; }
    private ChatClient() {}

    public void sendBroadcast(String text) {
        if (Client.out != null && text != null && !text.isBlank()) {
            Client.out.println("chat_all " + text);
        }
    }

    public void sendPrivate(int toId, String text) {
        if (Client.out != null && text != null && !text.isBlank()) {
            Client.out.println("chat_to " + toId + " " + text);
        }
    }

    public void sendMute(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_mute " + targetId);
        }
    }

    public void sendUnmute(int targetId) {
        if (Client.out != null) {
            Client.out.println("chat_unmute " + targetId);
        }
    }

    public void sendHelp() {
        if (Client.out != null) {
            Client.out.println("chat_help");
        }
    }
}
