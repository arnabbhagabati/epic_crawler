package com.epic.epiccrawler.main;

import com.epic.epiccrawler.crawlservice.YouTubeScraper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.epic.epiccrawler.EpicConstants.APPLICATION_NAME;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



@Component
public class ExecuteCrawl {
    //private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );
    private static final Logger LOG = LoggerFactory.getLogger(ExecuteCrawl.class);
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("(\\b(lol+|lmao+|lmfao+|rofl+|roflmao+|hehe+|haha+|hahaha+|bahaha+|bwahaha+|xD+|:D+|:-D+)\\b|😂|🤣|😆|😹|😄|😁|😅|😜|😝|🤪|🤭|👻)", Pattern.CASE_INSENSITIVE);
    //Pattern.compile("(\\b(lol+|lmao+|lmfao+|rofl+|roflmao+|hehe+|haha+|hahaha+|bahaha+|bwahaha+|xD+|:D+|:-D+)\\b|😂|🤣|😆|😹|😄|😁|😅|😜|😝|🤪|🤭|😛|🤡|😸|🙃|🥲|🤔|😎|🤷|😏|😬|🤨|🤯|😇|😈|👻)", Pattern.CASE_INSENSITIVE);

    private static final String API_KEY = "AIzaSyCj2csNua3EbkajBXlhfCAImkrAldOoFss";

    private static final Long MAX_COMMENT_THREAD = 75L;
    private static final Long MAX_REPLIES_PER_COMMENT_THREAD = 30L;
    private static final Long TOP_COMMENT_MIN_LIKES_THRESHOLD = 750L;

    private static final int MAX_CRAWL_COUNT = 7;

    @Autowired
    YouTubeScraper youTubeScraper;

    public void crawlComments(){
        //LOGGER.log( Level.FINE, "crawling through comments" );
        LOG.info("This is an INFO log");
        HashMap<String, JSONArray> commentsMap = new HashMap<>();
        Queue<String> videoQueue = new LinkedList<>();
        videoQueue.add("W8r-tXRLazs");
        int crawledCount =1;

        try {
            YouTube youtubeService = getService();

            while(!videoQueue.isEmpty()) {
                String currVideoId = videoQueue.poll();
                if(commentsMap.containsKey(currVideoId)){
                    continue;
                }
                crawledCount++;
                JSONArray parentJsonObject = getCommentsWithRepliesAsJson(youtubeService, currVideoId);
                //System.out.println(parentJsonObject.toString(2));  // Pretty print JSON
                commentsMap.put(currVideoId, parentJsonObject);
                if(commentsMap.size()>=MAX_CRAWL_COUNT){
                    break;
                }

                youTubeScraper.getRelatedVids(currVideoId,videoQueue);
                //addRelatedVidToQueue(currVideoId, youtubeService, videoQueue);
                // Print the HashMap
            }

            commentsMap.forEach((vidId, commentsArray) -> System.out.println(vidId + " " + commentsArray.toString(4)));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private YouTube getService() {
        return new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private JSONArray getCommentsWithRepliesAsJson(YouTube youtubeService, String videoId) throws IOException {
        JSONArray commentsArray = new JSONArray();  // Initialize JSON array to store filtered comments

        // Fetch the top-level comments ordered by relevance (Top Comments)
        YouTube.CommentThreads.List request = youtubeService.commentThreads()
                .list("snippet,replies")
                .setVideoId(videoId)
                .setKey(API_KEY)
                .setMaxResults(MAX_COMMENT_THREAD)  // Set the maximum number of comments to 20
                .setOrder("relevance");  // Fetch comments ordered by "Top Comments" (relevance)

        CommentThreadListResponse response = request.execute();
        List<CommentThread> commentThreads = response.getItems();

        // Loop through each comment thread (top-level comment)
        for (CommentThread commentThread : commentThreads) {
            JSONObject commentJson = new JSONObject();  // Create JSON object for each comment
            Comment topComment = commentThread.getSnippet().getTopLevelComment();

            /*if(!topComment.getSnippet().getTextOriginal().contains("Proof that having 3 beautiful women nodding")){
                continue;
            }*/
            Long topCommentLikesCount = topComment.getSnippet().getLikeCount();
            if(topCommentLikesCount<TOP_COMMENT_MIN_LIKES_THRESHOLD){
                continue;
            }

            // Get replies (if any) and fetch all replies using pagination
            List<Comment> allReplies = fetchAllReplies(youtubeService, commentThread);

            if (!allReplies.isEmpty()) {
                // Sort replies by the number of likes in descending order
                List<Comment> sortedReplies = allReplies.stream()
                        .sorted(Comparator.comparingLong(reply -> -reply.getSnippet().getLikeCount()))  // Sort by likes (descending)
                        .collect(Collectors.toList());

                boolean containsKeyword = false;
                JSONArray repliesArray = new JSONArray();  // Create JSON array for replies

                // Loop through the sorted replies and stop when a reply has fewer than 50 likes
                int addedRepliesCount = 0;
                for (Comment reply : sortedReplies) {
                    long likeCount = reply.getSnippet().getLikeCount();  // Use long for like count
                    String replyText = reply.getSnippet().getTextOriginal();

                    // Check if the reply contains any of the specified keywords or emojis
                    if (KEYWORD_PATTERN.matcher(replyText).find()) {
                        containsKeyword = true;
                    }

                    // Stop if the reply has fewer than 50 likes or if 20 replies have been added
                    if (likeCount >= 50L && addedRepliesCount < MAX_REPLIES_PER_COMMENT_THREAD) {
                        JSONObject replyJson = new JSONObject();
                        String replyAuthor = reply.getSnippet().getAuthorDisplayName();
                        replyJson.put("author", replyAuthor);
                        replyJson.put("replyText", replyText);
                        replyJson.put("likes", likeCount);  // Add number of likes for each reply
                        repliesArray.put(replyJson);  // Add reply to the replies array

                        addedRepliesCount++;
                    }

                    // Add the reply details to the JSON object

                }

                // If any reply contains the keywords, add the comment thread to the result
                if (containsKeyword) {
                    // Add replies array to the comment JSON object
                    commentJson.put("replies", repliesArray);

                    // Add the top-level comment details to the JSON object AFTER adding replies
                    String author = topComment.getSnippet().getAuthorDisplayName();
                    String commentText = topComment.getSnippet().getTextOriginal();
                    commentJson.put("author", author);
                    commentJson.put("commentText", commentText);
                    commentJson.put("likes", topCommentLikesCount);

                    // Add the comment JSON object to the comments array
                    commentsArray.put(commentJson);
                }
            }
        }

        return commentsArray;  // Return the array of filtered comments and their replies

    }


    private List<Comment> fetchAllReplies(YouTube youtubeService, CommentThread commentThread) throws IOException {
        List<Comment> allReplies = new ArrayList<>();

        // If there are more than 5 replies, paginate through to get all of them
        String parentId = commentThread.getSnippet().getTopLevelComment().getId();
        String nextPageToken = null;

        do {
            YouTube.Comments.List replyRequest = youtubeService.comments()
                    .list("snippet")
                    .setParentId(parentId)
                    .setKey(API_KEY)
                    .setMaxResults(100L);  // Fetch as many replies as possible per request (max 100)

            if (nextPageToken != null) {
                replyRequest.setPageToken(nextPageToken);
            }

            CommentListResponse replyResponse = replyRequest.execute();
            List<Comment> replies = replyResponse.getItems();

            allReplies.addAll(replies);

            nextPageToken = replyResponse.getNextPageToken();
            break;  // for now work with only 100 comments
        } while (nextPageToken != null);

        return allReplies;
    }

    public void addRelatedVidToQueue(String videoId, YouTube youtubeService,Queue<String> videoQueue) {

        YouTube.Search.List request = null;
        try {
            request = youtubeService.search()
                    .list("snippet")
                    .setRelatedToVideoId(videoId) // Get related videos by video ID
                    .setType("video")            // Specify that we're looking for videos
                    .setMaxResults(20L)           // Set the max number of results to return
                    .setKey(API_KEY);


            // Execute the API request and get the response
            SearchListResponse response = null;
            response = request.execute();


            // Extract the related video IDs from the response
            for (SearchResult result : response.getItems()) {
                String relatedVideoId = result.getId().getVideoId();
                videoQueue.add(relatedVideoId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
