// Facade over server messaging specifics for chat
class MessageFacade {
    // Basic formatting and limits
    private static final int MAX_LEN = 200;
    private static final int BACKLOG_LIMIT = 30;
    private static final long WINDOW_MS = 5000L; // rate limit window
    private static final int MAX_PER_WINDOW = 5; // max messages per window per user

    // Simple backlog (broadcast and system only)
    private static final java.util.LinkedList<MessageRecord> backlog = new java.util.LinkedList<>();

    // Rate limiting state
    private static final long[] windowStartMs = new long[Const.QTY_PLAYERS];
    private static final int[] countInWindow = new int[Const.QTY_PLAYERS];
    private static final boolean[] serverMuted = new boolean[Const.QTY_PLAYERS];

    // Mute matrix: muted[receiver][sender] == true means receiver will not see sender's messages
    private static final boolean[][] muted = new boolean[Const.QTY_PLAYERS][Const.QTY_PLAYERS];

    private static class MessageRecord {
        final int fromId; // use -1 for system
        final String text; // already formatted with timestamp and tags
        MessageRecord(int fromId, String text) {
            this.fromId = fromId;
            this.text = text;
        }
    }

    static void broadcastChat(int fromId, String message) {
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

    static void privateChat(int fromId, int toId, String message) {
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

    static void sendSystemAll(String text) {
        String formatted = formatWithTimestamp("[sys] " + normalize(text));
        synchronized (backlog) {
            backlog.add(new MessageRecord(-1, formatted));
            while (backlog.size() > BACKLOG_LIMIT) backlog.removeFirst();
        }
        ClientManager.sendToAllClients(-1 + " chat " + formatted);
    }

    static void sendSystemTo(int toId, String text) {
        if (!ClientManagerIsOnline(toId)) return;
        String formatted = formatWithTimestamp("[sys] " + normalize(text));
        ClientManager.sendToClient(toId, -1 + " chat " + formatted);
    }

    static void sendError(int toId, String text) {
        if (!ClientManagerIsOnline(toId)) return;
        String formatted = formatWithTimestamp("[error] " + normalize(text));
        ClientManager.sendToClient(toId, -1 + " chat " + formatted);
    }

    static void deliverBacklogTo(int toId) {
        if (!ClientManagerIsOnline(toId)) return;
        java.util.List<MessageRecord> snapshot;
        synchronized (backlog) { snapshot = new java.util.ArrayList<>(backlog); }
        for (MessageRecord rec : snapshot) {
            // Deliver each line as-is
            ClientManager.sendToClient(toId, rec.fromId + " chat " + rec.text);
        }
    }

    static void mutePlayer(int receiverId, int senderId) {
        if (receiverId >= 0 && receiverId < Const.QTY_PLAYERS && senderId >= 0 && senderId < Const.QTY_PLAYERS) {
            muted[receiverId][senderId] = true;
        }
    }

    static void unmutePlayer(int receiverId, int senderId) {
        if (receiverId >= 0 && receiverId < Const.QTY_PLAYERS && senderId >= 0 && senderId < Const.QTY_PLAYERS) {
            muted[receiverId][senderId] = false;
        }
    }

    private static boolean ClientManagerIsOnline(int id) {
        return id >= 0 && id < Const.QTY_PLAYERS && ClientManager.outById[id] != null;
    }

    private static boolean allowAndNormalizeMessage(int fromId, String message) {
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

    private static String normalize(String message) {
        if (message == null) return "";
        String m = message.trim();
        if (m.length() > MAX_LEN) m = m.substring(0, MAX_LEN);
        return m;
    }

    private static String formatWithTimestamp(String message) {
        String m = normalize(message);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        String ts = sdf.format(new java.util.Date());
        return "[" + ts + "] " + m;
    }

    // ===================== Weather lookup =====================
    static void requestWeather(int fromId, String query) {
        String q = normalize(query);
        if (q.isEmpty()) {
            sendError(fromId, "Usage: /weather <city or country>");
            return;
        }
        new Thread(() -> {
            try {
                // 1) Geocode name -> lat/lon using Open-Meteo Geocoding (no API key)
                String qEnc = java.net.URLEncoder.encode(q, "UTF-8");
                String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + qEnc + "&count=1&language=en&format=json";
                String geoJson = httpGet(geoUrl);
                if (geoJson == null || geoJson.isEmpty() || !geoJson.contains("\"results\"")) {
                    sendError(fromId, "Weather: location not found for '" + q + "'.");
                    return;
                }
                int resIdx = geoJson.indexOf("\"results\"");
                double lat = parseDoubleAfter(geoJson, "\"latitude\":", resIdx);
                double lon = parseDoubleAfter(geoJson, "\"longitude\":", resIdx);
                String placeName = parseStringAfter(geoJson, "\"name\":\"", resIdx);
                String country = parseStringAfter(geoJson, "\"country\":\"", resIdx);
                if (Double.isNaN(lat) || Double.isNaN(lon)) {
                    sendError(fromId, "Weather: couldn't resolve coordinates for '" + q + "'.");
                    return;
                }

                // 2) Fetch current weather
                String wUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true&timezone=auto";
                String wJson = httpGet(wUrl);
                if (wJson == null || wJson.isEmpty() || !wJson.contains("\"current_weather\"")) {
                    sendError(fromId, "Weather: couldn't fetch current weather.");
                    return;
                }
                int cwIdx = wJson.indexOf("\"current_weather\"");
                double temp = parseDoubleAfter(wJson, "\"temperature\":", cwIdx);
                double wind = parseDoubleAfter(wJson, "\"windspeed\":", cwIdx);
                int code = (int) Math.round(parseDoubleAfter(wJson, "\"weathercode\":", cwIdx));
                String description = weatherCodeToText(code);

                String where = (placeName != null && !placeName.isEmpty() ? placeName : q);
                if (country != null && !country.isEmpty()) where += ", " + country;
                String msg = String.format("Weather in %s: %.1f°C, wind %.1f m/s, %s", where, temp, wind, description);
                sendSystemAll(msg);
            } catch (Exception ex) {
                sendError(fromId, "Weather error: " + ex.getMessage());
            }
        }).start();
    }

    private static String httpGet(String urlStr) throws java.io.IOException {
        java.net.URL url;
        try {
            url = java.net.URI.create(urlStr).toURL();
        } catch (java.net.MalformedURLException e) {
            throw new java.io.IOException("Bad URL: " + urlStr, e);
        }
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(7000);
        try (java.io.InputStream in = conn.getInputStream();
             java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static double parseDoubleAfter(String text, String key, int start) {
        int idx = text.indexOf(key, start);
        if (idx < 0) return Double.NaN;
        idx += key.length();
        int end = idx;
        while (end < text.length() && "-+.0123456789".indexOf(text.charAt(end)) >= 0) end++;
        try { return Double.parseDouble(text.substring(idx, end)); } catch (Exception e) { return Double.NaN; }
    }

    private static String parseStringAfter(String text, String key, int start) {
        int idx = text.indexOf(key, start);
        if (idx < 0) return null;
        idx += key.length();
        int end = text.indexOf('"', idx);
        if (end < 0) return null;
        return text.substring(idx, end);
    }

    private static String weatherCodeToText(int code) {
        switch (code) {
            case 0: return "Clear sky";
            case 1: return "Mainly clear";
            case 2: return "Partly cloudy";
            case 3: return "Overcast";
            case 45: case 48: return "Fog";
            case 51: case 53: case 55: return "Drizzle";
            case 56: case 57: return "Freezing drizzle";
            case 61: case 63: case 65: return "Rain";
            case 66: case 67: return "Freezing rain";
            case 71: case 73: case 75: return "Snow";
            case 77: return "Snow grains";
            case 80: case 81: case 82: return "Rain showers";
            case 85: case 86: return "Snow showers";
            case 95: return "Thunderstorm";
            case 96: case 99: return "Thunderstorm with hail";
            default: return "Weather code " + code;
        }
    }

    // ===================== Votes (Kick / Mute) =====================
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
    private static final java.util.Map<String, Vote> activeVotes = new java.util.HashMap<>();

    static void voteKick(int fromId, int targetId) {
        startOrVote(fromId, targetId, VoteType.KICK, true);
    }

    static void voteMute(int fromId, int targetId) {
        startOrVote(fromId, targetId, VoteType.MUTE, true);
    }

    static void voteKickChoice(int fromId, int targetId, boolean yes) {
        startOrVote(fromId, targetId, VoteType.KICK, yes);
    }

    static void voteMuteChoice(int fromId, int targetId, boolean yes) {
        startOrVote(fromId, targetId, VoteType.MUTE, yes);
    }

    private static void startOrVote(int fromId, int targetId, VoteType type, boolean yes) {
        if (fromId < 0 || fromId >= Const.QTY_PLAYERS || ClientManager.outById[fromId] == null) return;
        if (targetId < 0 || targetId >= Const.QTY_PLAYERS || !ClientManagerIsOnline(targetId)) {
            sendError(fromId, "Target " + targetId + " is not online.");
            return;
        }
        if (fromId == targetId && type == VoteType.KICK) {
            sendError(fromId, "You cannot kick yourself.");
            return;
        }
        // expire old vote if needed
        String key = type.name() + ":" + targetId;
        Vote v = activeVotes.get(key);
        long now = System.currentTimeMillis();
        if (v != null && now > v.endMs) {
            activeVotes.remove(key);
            sendSystemAll("Vote to " + type.name().toLowerCase() + " player " + targetId + " expired.");
            v = null;
        }
        // create if absent
        if (v == null) {
            v = new Vote(type, targetId);
            activeVotes.put(key, v);
            sendSystemAll("Vote started to " + type.name().toLowerCase() + " player " + targetId + ". Type /" +
                (type == VoteType.KICK ? "votekick " : "votemute ") + targetId + " to vote yes, or /vote " +
                (type == VoteType.KICK ? "kick " : "mute ") + targetId + " no to vote no. (30s)");
        }
        // record vote
        if (v.setVote(fromId, yes)) {
            announceTally(v);
        } else {
            sendError(fromId, "You've already voted.");
        }
        // check success
        checkAndFinalize(v);
    }

    private static void announceTally(Vote v) {
        int yes = countYes(v);
        int no = countNo(v);
        int needed = requiredYes(v.targetId);
        sendSystemAll("Vote to " + v.type.name().toLowerCase() + " player " + v.targetId + ": " + yes + "/" + needed + " yes (" + no + " no)");
    }

    private static void checkAndFinalize(Vote v) {
        int yes = countYes(v);
        int needed = requiredYes(v.targetId);
        if (yes >= needed) {
            // Passed
            activeVotes.remove(v.type.name() + ":" + v.targetId);
            if (v.type == VoteType.KICK) {
                sendSystemAll("Vote passed. Kicking player " + v.targetId + ".");
                ClientManager.disconnectPlayer(v.targetId);
            } else {
                serverMuted[v.targetId] = true;
                sendSystemAll("Vote passed. Muted player " + v.targetId + ".");
            }
        }
    }

    private static int countYes(Vote v) {
        int c = 0;
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            if (v.voteOf(i) == 1 && ClientManagerIsOnline(i)) c++;
        }
        return c;
    }

    private static int countNo(Vote v) {
        int c = 0;
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            if (v.voteOf(i) == -1 && ClientManagerIsOnline(i)) c++;
        }
        return c;
    }

    private static int requiredYes(int targetId) {
        int online = 0;
        for (int i = 0; i < Const.QTY_PLAYERS; i++) if (ClientManagerIsOnline(i)) online++;
        if (ClientManagerIsOnline(targetId)) online--; // exclude target from quorum
        if (online < 1) online = 1;
        return online / 2 + 1; // strict majority of voters
    }
}
