import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class WeatherService {
    private static WeatherService instance = new WeatherService();
    public static WeatherService getInstance() { return instance; }
    private WeatherService() {}

    public void requestWeather(int fromId, String query) {
        String q = normalize(query);
        if (q.isEmpty()) {
            ChatService.getInstance().sendError(fromId, "Usage: /weather <city or country>");
            return;
        }
        new Thread(() -> {
            try {
                // 1) Geocode name -> lat/lon using Open-Meteo Geocoding (no API key)
                String qEnc = URLEncoder.encode(q, "UTF-8");
                String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + qEnc + "&count=1&language=en&format=json";
                String geoJson = httpGet(geoUrl);
                if (geoJson == null || geoJson.isEmpty() || !geoJson.contains("\"results\"")) {
                    ChatService.getInstance().sendError(fromId, "Weather: location not found for '" + q + "'.");
                    return;
                }
                int resIdx = geoJson.indexOf("\"results\"");
                double lat = parseDoubleAfter(geoJson, "\"latitude\":", resIdx);
                double lon = parseDoubleAfter(geoJson, "\"longitude\":", resIdx);
                String placeName = parseStringAfter(geoJson, "\"name\":\"", resIdx);
                String country = parseStringAfter(geoJson, "\"country\":\"", resIdx);
                if (Double.isNaN(lat) || Double.isNaN(lon)) {
                    ChatService.getInstance().sendError(fromId, "Weather: couldn't resolve coordinates for '" + q + "'.");
                    return;
                }

                // 2) Fetch current weather
                String wUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true&timezone=auto";
                String wJson = httpGet(wUrl);
                if (wJson == null || wJson.isEmpty() || !wJson.contains("\"current_weather\"")) {
                    ChatService.getInstance().sendError(fromId, "Weather: couldn't fetch current weather.");
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
                ChatService.getInstance().sendSystemAll(msg);
            } catch (Exception ex) {
                ChatService.getInstance().sendError(fromId, "Weather error: " + ex.getMessage());
            }
        }).start();
    }

    private String normalize(String message) {
        if (message == null) return "";
        return message.trim();
    }

    private String httpGet(String urlStr) throws IOException {
        URL url;
        try {
            url = URI.create(urlStr).toURL();
        } catch (MalformedURLException e) {
            throw new IOException("Bad URL: " + urlStr, e);
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(7000);
        try (InputStream in = conn.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private double parseDoubleAfter(String text, String key, int start) {
        int idx = text.indexOf(key, start);
        if (idx < 0) return Double.NaN;
        idx += key.length();
        int end = idx;
        while (end < text.length() && "-+.0123456789".indexOf(text.charAt(end)) >= 0) end++;
        try { return Double.parseDouble(text.substring(idx, end)); } catch (Exception e) { return Double.NaN; }
    }

    private String parseStringAfter(String text, String key, int start) {
        int idx = text.indexOf(key, start);
        if (idx < 0) return null;
        idx += key.length();
        int end = text.indexOf('"', idx);
        if (end < 0) return null;
        return text.substring(idx, end);
    }

    private String weatherCodeToText(int code) {
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
}
