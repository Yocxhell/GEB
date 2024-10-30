package com.yocxhell.robottest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class is designed to fetch a Steam ID from a Steam custom ID URL using Steam.io site
public class SteamIdFetcher {
    
    // Constants for timeouts and regex
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final String STEAM_ID_REGEX = "(\\d{17})";

    // Method to fetch the Steam ID from a custom Steam URL
    public static String fetchId(String steamCustomIdUrl) {
        if (steamCustomIdUrl == null || steamCustomIdUrl.trim().isEmpty()) {
            System.err.println("Invalid or empty steam custom ID URL provided");
            return "";
        }

        try {
            String response = fetchSteamIdHtml(steamCustomIdUrl);
            if (response != null) {
                return extractSteamIdFromHtml(response);
            } else {
                System.out.println("\nNo response from server or empty content\n");
            }
        } catch (Exception e) {
            System.err.println("\nError fetching Steam ID: " + e.getMessage());
        }

        return "";
    }

    // Helper method to perform the HTTP GET request
    private static String fetchSteamIdHtml(String steamCustomIdUrl) throws Exception {
        System.out.print("\nFetching from: " + steamCustomIdUrl + "\n");
        URL url = new URL("https://steamid.io/lookup/" + steamCustomIdUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);  // Set connection timeout
        connection.setReadTimeout(READ_TIMEOUT);           // Set read timeout

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        }
    }

    // Helper method to extract Steam ID from the HTML content using regex
    private static String extractSteamIdFromHtml(String htmlContent) {
        Pattern pattern = Pattern.compile(STEAM_ID_REGEX);
        Matcher matcher = pattern.matcher(htmlContent);

        if (matcher.find()) {
            //If id is extractable, create steam id url with id extracted
            String steamIdUrl = "https://steamcommunity.com/profiles/" + matcher.group(1) + "/";
            //System.out.println("\nFetched Steam ID: " + steamIdUrl + "\n");
            return steamIdUrl;
        } else {
            //System.out.println("\nSteam ID not fetchable\n");
            return "";
        }
    }
}
