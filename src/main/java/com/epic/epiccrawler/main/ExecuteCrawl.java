package com.epic.epiccrawler.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.javapoet.ClassName;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
//import java.util.logging.Logger;

@Component
public class ExecuteCrawl {
    //private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );
    private static final Logger LOG = LoggerFactory.getLogger(ExecuteCrawl.class);
    public void crawlComments(){
        //LOGGER.log( Level.FINE, "crawling through comments" );
        LOG.info("This is an INFO log");
    }
}
