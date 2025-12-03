class WeatherClient {
    private static WeatherClient instance = new WeatherClient();
    public static WeatherClient getInstance() { return instance; }
    private WeatherClient() {}

    public void sendWeather(String query) {
        if (Client.out != null && query != null && !query.isBlank()) {
            Client.out.println("chat_weather " + query);
        }
    }
}
