package com.epic.epiccrawler.crawlservice;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YouTubeScraper {

    // Method to scrape related videos from a YouTube video page
    public List<String> getRelatedVideos(String videoId) {
        // Fetch the YouTube video page HTML
        List<String> relatedVideos = new ArrayList<>();
        try {
            Document doc = null;
            String videoUrl = "https://www.youtube.com/watch?v="+videoId;
            doc = Jsoup.connect(videoUrl).get();

            // List to store the URLs of related videos


            // Find the elements that contain related video links
            Elements relatedVideoElements = doc.select("a#thumbnail");

            // Loop through and extract the href (link) attribute for each related video
            for (Element element : relatedVideoElements) {
                String relatedVideoUrl = element.attr("href");

                // Filter out non-video links and add valid video URLs to the list
                if (relatedVideoUrl.contains("/watch")) {
                    String fullUrl = "https://www.youtube.com" + relatedVideoUrl;
                    relatedVideos.add(fullUrl);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return relatedVideos;  // Return the list of related video URLs
    }


    public void sendGetRequest(String urlStr) {
        // Create a URL object
        try {
            URL url = new URL(urlStr);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // If response code is 200 (HTTP OK), read the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Read the response line by line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                System.out.println("Response Content:");
                System.out.println(response.toString());
            } else {
                System.out.println("GET request failed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getRelatedVids(String videoId, Queue<String> videoQueue, Set<String> allVIds) {

        String urlStr = "https://www.youtube.com/watch?v="+videoId;
        try {
            URL url = new URL(urlStr);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // If response code is 200 (HTTP OK), read the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Read the response line by line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                //System.out.println("Response Content:");
                //System.out.println(response.toString());
                String regex = "\"videoId\":\"([^\"]+)\"";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response.toString());

                while (matcher.find()) {
                    String nextRelVid = matcher.group(1);  // Extract the video ID (group 1)
                    if(!allVIds.contains(nextRelVid)){
                        videoQueue.add(nextRelVid);
                        allVIds.add(nextRelVid);
                    }

                }


            } else {
                System.out.println("GET request failed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

