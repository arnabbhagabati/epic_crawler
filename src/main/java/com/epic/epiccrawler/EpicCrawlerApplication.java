package com.epic.epiccrawler;

import com.epic.epiccrawler.main.ExecuteCrawl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.epic.epiccrawler")
public class EpicCrawlerApplication {

	private static ExecuteCrawl executeCrawl;

	@Autowired
	public void setSomeThing(ExecuteCrawl executeCrawl){
		EpicCrawlerApplication.executeCrawl = executeCrawl;
	}


	public static void main(String[] args) {
		SpringApplication.run(EpicCrawlerApplication.class, args);
		executeCrawl.crawlComments();
	}

}
