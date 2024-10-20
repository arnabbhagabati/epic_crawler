package com.epic.epiccrawler.crawlservice;

import com.epic.epiccrawler.HTTP.HTTPConnectionService;
import com.epic.epiccrawler.main.ExecuteCrawl;
import com.epic.epiccrawler.util.LanguageUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.epic.epiccrawler.EpicConstants.MAX_CRAWL_COUNT;

@Service
public class YouTubeScraper {

    @Autowired
    HTTPConnectionService httpConnectionService;

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteCrawl.class);

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


    public void getRelatedVids(String videoId, Queue<String> videoQueue, Set<String> allVIds, Set<String> rejectedVids, int currCrawledSize) {

        String urlStr = "https://www.youtube.com/watch?v="+videoId;
        try {

            HttpURLConnection connection = httpConnectionService.getHTTPConnection(urlStr);
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

                int addedRelVidsCount =0;

                while (matcher.find()) {
                    if((addedRelVidsCount + currCrawledSize) > (2*MAX_CRAWL_COUNT)){
                        break;
                    }
                    String nextRelVid = matcher.group(1);  // Extract the video ID (group 1)
                    if(rejectedVids.contains(nextRelVid)){
                        continue;
                    }else{
                        if (!allVIds.contains(nextRelVid) && isEmbeddable(nextRelVid)) {
                            videoQueue.add(nextRelVid);
                            allVIds.add(nextRelVid);
                            addedRelVidsCount++;
                        } else {
                            rejectedVids.add(nextRelVid);
                        }
                    }
                }

            } else {
                System.out.println("getRelatedVids GET request failed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public boolean isEmbeddable(String videoId){
        String urlStr = "https://www.youtube.com/embed/"+videoId;
        boolean embeddable = false;
        try {

            HttpURLConnection connection = httpConnectionService.getHTTPConnection(urlStr);
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
                String responseString = response.toString();
                //Todo : reduce string size/ make this search more efficient
                if(!LanguageUtilities.containsNonEnglishCharacters(responseString) && !responseString.contains("Video unavailable")){
                    embeddable = true;
                }else{
                    LOG.info("This video is not embeddable "+urlStr);
                }

            } else {
                System.out.println("GET request failed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return embeddable;
    }


}

