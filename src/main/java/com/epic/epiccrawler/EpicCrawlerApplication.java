package com.epic.epiccrawler;

import com.epic.epiccrawler.crawlservice.YouTubeScraper;
import com.epic.epiccrawler.main.ExecuteCrawl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootApplication
@ComponentScan("com.epic.epiccrawler")
public class EpicCrawlerApplication {

	private static ExecuteCrawl executeCrawl;

	@Autowired
	public void setExecuteCrawl(ExecuteCrawl executeCrawl){
		EpicCrawlerApplication.executeCrawl = executeCrawl;
	}


	public static void main(String[] args) {
		SpringApplication.run(EpicCrawlerApplication.class, args);
		executeCrawl.crawlComments();
		//String videoUrl = "https://www.youtube.com/watch?v=W8r-tXRLazs";
		//new YouTubeScraper().sendGetRequest(videoUrl);

		// Print the list of related video URLs
		/*for (String video : relatedVideos) {
			System.out.println("Related Video URL: " + video);
		}*/
	}

}
